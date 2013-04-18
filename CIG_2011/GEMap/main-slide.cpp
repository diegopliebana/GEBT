#include<iostream>
#include<fstream>
#include<sstream>
#include<ctime>
#include<cfloat>

#include "qGA.h"
#include "GEMap.h"

using namespace std;

double evaluate(const string &phenotype, const GEMap &mapper, const qGA &pop, const size_t &gen,
	int parIndex, int argc, char **argv){
	// If using gen argument as seed;
	//size_t iSeed = gen;
	//size_t fSeed = gen;
	// If running on 5 cases;
	size_t iSeed = gen + 1;
	size_t fSeed = gen + 5;
	// 1) Write phenotype to file;
	ofstream phenoFile;
	stringstream pFile;
	pFile << "../BTs/phenotype-slide-" << pop.getRandomSeed() << "-" << parIndex << ".xml";
	phenoFile.open(pFile.str().c_str());
	phenoFile << phenotype << "\n";
	phenoFile.close();
	// 2) Cycle through all seeds;
	double fitness = 0;
	for(size_t seed = iSeed ; seed <= fSeed; ++seed){
		// 3) System call Mario;
		stringstream sysCall;
		sysCall << "rm ../BTs/fitness-slide-" << pop.getRandomSeed() << "-" << parIndex << ".txt ; "
			<< "cd ../benchmark_0_1_5/MarioAI+Benchmark"
			<< " && java -classpath \"build/classes/\""
			<< " grammaticalbehaviors.GEBT_Mario.EvoMain"
			<< " -tl 100 -ld 0 1 2 3 4 -lt 0 1 -ll 320";
			//<< " -tl 100 -ld 0 1 2 3 4 5 6 7 8 -lt 0 1 -ll 320";
		if(argc != 3) sysCall
			<< " -ce 1 -vis 0 -fps 100 -rnd " << seed;
		else sysCall
			<< " -ce 1 -vis " << argv[1] << " -fps " << argv[2] << " -rnd " << seed;
		sysCall
			<< " -if ../../BTs/phenotype-slide-" << pop.getRandomSeed() << "-" << parIndex << ".xml"
			<< " -of ../../BTs/fitness-slide-" << pop.getRandomSeed() << "-" << parIndex << ".txt"
			<< " -os /dev/null >> /dev/null";
		system(sysCall.str().c_str());
		// 4) Recover fitness;
		stringstream fFile;
		fFile << "../BTs/fitness-slide-" << pop.getRandomSeed() << "-" << parIndex << ".txt";
		ifstream fitFile(fFile.str().c_str(),ios::in);
		if(!fitFile.is_open()){
			cerr << "WARNING: Error recovering fitness file '"
				<< fFile.str() << "'.\n";
		}
		else{
			string fitnessString;
			getline(fitFile, fitnessString);
			fitness += atof(fitnessString.c_str());
			fitFile.close();
		}
	}
	return fitness / (fSeed - iSeed + 1);
}

int main(int argc, char **argv){
	// Check params;
	if(argc < 2){
		cout << "Usage:\n"
			<< qGA::getHelpString()
			<< GEMap::getHelpString()
			<< "\tbool\t\tVisualise Mario\n"
			<< "\tint\t\tFPS for Mario\n";
		exit(0);
	}
	if(qGA::paramClash(GEMap::getParamsString())){
		cout << "Parameters clash.\n";
		return 1;
	}
	// GA;
	qGA pop;
	pop.extractParams(argc, argv);
	// Mapper;
	GEMap mapper;
	mapper.extractParams(argc, argv);
	if(!mapper.readBNFFile(mapper.getGrammarFile(), true)){
		exit(0);
	}
	// Save params;
	ofstream paramFile((pop.getXPName() + "-PARAMS.dat").c_str());
	paramFile << pop.outputParams() << mapper.outputParams();
	for(int ii = 1; ii < argc; ++ii){
		paramFile << " " << argv[ii];
	}
	paramFile << "\n";
	paramFile.close();
	///////////////////////////////////////////////////////////////////////
	// Initialisation;
	///////////////////////////////////////////////////////////////////////
	//#pragma omp parallel for
	for(size_t ind = 0; ind < pop.size(); ++ind){
		string phenotype;
		if ((pop[ind].valid = mapper.initGE(pop[ind].genotype, phenotype,
			pop[ind].effectiveSize, pop[ind].xoSites,
			// index
			// If grow uses only max length;
			(ind % 2? 1.0 : double(ind) / pop.size()),
			// If grow uses different max lengths;
			//(ind / pop.size()),
			// Grow?
			//(ind < pop.size() / 2)))){
			bool(ind % 2)))){
			pop[ind].fitness = evaluate(phenotype, mapper, pop, 0, ind, argc, argv);
		}
		else pop[ind].fitness = 0;
	}
	///////////////////////////////////////////////////////////////////////
	// Evolutionary cycle;
	///////////////////////////////////////////////////////////////////////
	for(size_t gen = 0; gen < pop.getMaxGens(); ++gen){
		// OUTPUT STATS;
		pop.outputStats(gen, gen);
		// REEVALUATE PARENTS (if using different seeds);
		/*
		#pragma omp parallel for
		for(int ind = 0; ind < pop.size(); ++ind){
			string phenotype;
			if((pop[ind].valid = mapper.mapGE(pop[ind].genotype, phenotype,
				pop[ind].effectiveSize))){
				//pop[ind].fitness += evaluate(phenotype, mapper, pop, gen + 1,
				//	ind, argc, argv);
				//pop[ind].fitness /= 2;
				pop[ind].fitness = evaluate(phenotype, mapper, pop, gen + 1,
					ind, argc, argv);
			}
			else{
				pop[ind].fitness = 0;
			}
		}
		*/
		// GENERATE OFFSPRING;
		qGA offspring;
		pop.generateOffspring(offspring);
		// EVALUATE OFFSPRING;
		//#pragma omp parallel for
		for(size_t ind = 0; ind < offspring.size(); ++ind){
			string phenotype;
			if (!offspring[ind].valid && (offspring[ind].valid =
				mapper.mapGE(offspring[ind].genotype, phenotype,
				offspring[ind].effectiveSize, offspring[ind].xoSites,
				offspring[ind].extra))){
				offspring[ind].fitness = evaluate(phenotype, mapper, pop, gen + 1,
					ind, argc, argv);
			}
			else if(!offspring[ind].valid){
				offspring[ind].fitness = 0;
			}
		}
		// MERGE OFFSPRING;
		pop.replace(offspring);
		// Save best individual;
		ofstream bestFile;
		bestFile.open((pop.getXPName() + "-BEST-gen_fit_phen.dat").c_str(), ios::app);
		string phenotype;
		mapper.mapGE(pop[pop.getBestFitIndex()].genotype, phenotype,
			pop[pop.getBestFitIndex()].effectiveSize,
			pop[pop.getBestFitIndex()].xoSites,
			pop[pop.getBestFitIndex()].extra);
		bestFile << gen << "\t" << pop[pop.getBestFitIndex()].fitness
			<< "\t" << phenotype << "\n";
		bestFile.close();
		// Save it to BTs as well;
		ofstream phenoFile;
		stringstream bestPhenoFile;
		bestPhenoFile << "../BTs/best-slide-" << pop.getRandomSeed() << "-" <<
			gen + 1 << "-" <<
			pop[pop.getBestFitIndex()].fitness << ".xml";
		phenoFile.open(bestPhenoFile.str().c_str());
		phenoFile << phenotype << "\n";
		phenoFile.close();
		// Signal generation done, move along;
		cout << "Gen " << gen + 1 << ": " << pop[pop.getBestFitIndex()].fitness << "\n";
	}
	// OUTPUT FINAL STATS;
	pop.outputStats(pop.getMaxGens(), pop.getMaxGens());
	return 0;
}

