// qGA.cpp -*- C++ -*-
#ifndef _QGA_CPP_
#define _QGA_CPP_

#include <vector>
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <climits>
#include <cfloat>
#include <cassert>
#include <algorithm>
#include <cmath>
#include <set>
#include <unistd.h>
#include <cstring>

#include "qGA.h"

using namespace std;

qGA::qGA(){
	popSize		= QGA_DEFAULT_POP;
	maxGens		= QGA_DEFAULT_GENS;
	selector	= QGA_DEFAULT_SELECTOR;
	tSizeRatio	= QGA_DEFAULT_TSIZERATIO;
	fairTournament	= QGA_DEFAULT_FAIRTOURNAMENT;
	XORate		= QGA_DEFAULT_XO;
	swapRate	= QGA_DEFAULT_SWAP;
	intMutRate	= QGA_DEFAULT_INTMUT;
	avgIntMutRate= QGA_DEFAULT_AVGINTMUT;
	exclusiveOps	= QGA_DEFAULT_EXCLUSIVEOPS;
	replacement	= QGA_DEFAULT_REPLACEMENT;
	elitismRatio	= QGA_DEFAULT_ELITISMRATIO;
	maximising	= QGA_DEFAULT_MAXIMISE;
	effectiveOps	= QGA_DEFAULT_EFFECTIVEOPS;
	markedXO	= QGA_DEFAULT_MARKEDXO;
	XONbrPoints	= QGA_DEFAULT_XONBRPOINTS;
	samePointXO	= QGA_DEFAULT_SAMEPOINTXO;
	shcc		= QGA_DEFAULT_SHCC;
	whcc		= QGA_DEFAULT_WHCC;
	replaceIllegals	= QGA_DEFAULT_REPLACEILLEGALS;
	maxCodonValue	= QGA_DEFAULT_MAXCODONVALUE;
	maxGenomeLength	= QGA_DEFAULT_MAXGENOMELENGTH;
	XPName		= QGA_DEFAULT_XPNAME;
	randomSeed	= QGA_DEFAULT_SEED;
	tournamentPicks.clear();
	resize(popSize);
	// Setup individual IDs;
	for(size_t ii = 0; ii < popSize; ++ii) at(ii).ID = ii + 1;
	// Flag uncounted invalids;
	invalidOffspring = -1;
}

///////////////////////////////////////////////////////////////////////////////
// Settings
///////////////////////////////////////////////////////////////////////////////

// Genotype sorting helper functions;
bool genotypeFitGTCmp(const qGAIndividual&, const qGAIndividual&);
bool genotypeFitLTCmp(const qGAIndividual&, const qGAIndividual&);

string qGA::getParamsString(){
	return QGA_PARAMSSTRING;
}

string qGA::getHelpString(){
	stringstream helpStr;
	helpStr
		<< "\t -p int \t population size (default "
		<< QGA_DEFAULT_POP << ")\n"
		<< "\t -g int \t number of generations (default "
		<< QGA_DEFAULT_GENS << ")\n"
		<< "\t -s str \t selector (default "
		<< QGA_STRSELECTOR[QGA_DEFAULT_SELECTOR] << "); choices are\n";
	for(size_t ii = 0; ii < QGA_TOTSELECTOR; ++ii){
		helpStr << "\t\t\t " << QGA_STRSELECTOR[ii] << ":\t" << QGA_DETAILSSELECTOR[ii] << "\n";
	}
	helpStr
		<< "\t -t dbl \t tournament size ratio (default "
		<< QGA_DEFAULT_TSIZERATIO << ")\n"
		<< "\t -f bool \t fair tournament (default "
		<< QGA_DEFAULT_FAIRTOURNAMENT << " = " << (QGA_DEFAULT_FAIRTOURNAMENT?"yes":"no") << ")\n"
		<< "\t -x dbl \t crossover ratio (default "
		<< QGA_DEFAULT_XO << ")\n"
		<< "\t -w dbl \t swap ratio (if using marked xo; applied only if -x fails) (default "
		<< QGA_DEFAULT_SWAP << ")\n"
		<< "\t -i dbl \t per integer mutation ratio (default "
		<< QGA_DEFAULT_INTMUT << ")\n"
		<< "\t -a dbl \t average integer mutation ratio (default "
		<< QGA_DEFAULT_AVGINTMUT << ")\n"
		<< "\t -o bool \t exclusive operators (default "
		<< QGA_DEFAULT_EXCLUSIVEOPS << " = " << (QGA_DEFAULT_EXCLUSIVEOPS?"yes":"no") << ")\n"
		<< "\t -r str \t replacement (default "
		<< QGA_STRREPLACEMENT[QGA_DEFAULT_REPLACEMENT] << "); choices are\n";
	for(size_t ii = 0; ii < QGA_TOTREPLACEMENT; ++ii){
		helpStr << "\t\t\t " << QGA_STRREPLACEMENT[ii] << ":\t" << QGA_DETAILSREPLACEMENT[ii] << "\n";
	}
	helpStr
		<< "\t -e dbl \t elitism ratio (default "
		<< QGA_DEFAULT_ELITISMRATIO << ")\n"
		<< "\t -m bool \t maximise fitness (default "
		<< QGA_DEFAULT_MAXIMISE << " = " << (QGA_DEFAULT_MAXIMISE?"yes":"no") << ")\n"
		<< "\t -E bool \t effective operators (default "
		<< QGA_DEFAULT_EFFECTIVEOPS << " = " << (QGA_DEFAULT_EFFECTIVEOPS?"yes":"no") << ")\n"
		<< "\t -M bool \t marked crossover (default "
		<< QGA_DEFAULT_MARKEDXO << " = " << (QGA_DEFAULT_MARKEDXO?"yes":"no") << ")\n"
		<< "\t -n bool \t number of crossover points (default "
		<< QGA_DEFAULT_XONBRPOINTS << ")\n"
		<< "\t -X bool \t same point crossover (default "
		<< QGA_DEFAULT_SAMEPOINTXO << " = " << (QGA_DEFAULT_SAMEPOINTXO?"yes":"no") << ")\n"
		<< "\t -H bool \t strong headless chicken crossover (default "
		<< QGA_DEFAULT_SHCC << " = " << (QGA_DEFAULT_SHCC?"yes":"no") << ")\n"
		<< "\t -h bool \t weak headless chicken crossover (default "
		<< QGA_DEFAULT_WHCC << " = " << (QGA_DEFAULT_WHCC?"yes":"no") << ")\n"
		<< "\t -I bool \t replace illegal offspring (default "
		<< QGA_DEFAULT_REPLACEILLEGALS << " = " << (QGA_DEFAULT_REPLACEILLEGALS?"yes":"no") << ")\n"
		<< "\t -c int \t max codon value (default "
		<< QGA_DEFAULT_MAXCODONVALUE << ")\n"
		<< "\t -l int \t max genotype length (default "
		<< QGA_DEFAULT_MAXGENOMELENGTH << ")\n"
		<< "\t -F str \t experiment name (default "
		<< QGA_DEFAULT_XPNAME << ")\n"
		<< "\t -S int \t random seed (default time; now = " << QGA_DEFAULT_SEED<< ")\n";
	return helpStr.str();
}

bool qGA::paramClash(const string &otherParamsString){
	string myParamsString = QGA_PARAMSSTRING;
	for(size_t ii = 0; ii < myParamsString.size(); ++ii){
		for(size_t jj = 0; jj < otherParamsString.size(); ++jj){
			if(myParamsString[ii] == ':') continue;
			if(myParamsString[ii] == otherParamsString[jj]) return true;
		}
	}
	return false;
}

///////////////////////////////////////////////////////////////////////////////
// Receives argc and argv, sets argument values according to options,
// but leaves argc and argv unchanged.
///////////////////////////////////////////////////////////////////////////////
void qGA::scanParams(int &argc, char **argv){
	return extractParams(argc, argv, false);
}

///////////////////////////////////////////////////////////////////////////////
// Receives argc and argv, sets argument values according to options,
// and extracts the relevant parameters if modifyArgv is true.
///////////////////////////////////////////////////////////////////////////////
void qGA::extractParams(int &argc, char **argv, bool modifyArgv){
	opterr = 0;		// Don't quit if unknown params are found;
	optind = 1;		// Reset scanner;
	optopt = 0;		// Last unknown "valid" argument found;
	vector<char*> newargv;	// New argc and argv to return, with extracted options;
	char opt;
	while(optind < argc){
		opt = getopt(argc, argv, QGA_PARAMSSTRING);
		switch(opt){
			case 'p': setPopSize(atoi(optarg));
				break;
			case 'g': setMaxGens(atoi(optarg));
				break;
			case 's': setSelectorStr(optarg);
				break;
			case 't': setTSizeRatio(atof(optarg));
				break;
			case 'f': setFairTournament(atoi(optarg));
				break;
			case 'x': setXORate(atof(optarg));
				break;
			case 'w': setSwapRate(atof(optarg));
				break;
			case 'i': setIntMutRate(atof(optarg));
				break;
			case 'a': setAvgIntMutRate(atof(optarg));
				break;
			case 'o': setExclusiveOps(atoi(optarg));
				break;
			case 'r': setReplacementStr(optarg);
				break;
			case 'e': setElitismRatio(atof(optarg));
				break;
			case 'm': setMaximising(atoi(optarg));
				break;
			case 'E': setEffectiveOps(atoi(optarg));
				break;
			case 'M': setMarkedXO(atoi(optarg));
				break;
			case 'n': setXONbrPoints(atoi(optarg));
				break;
			case 'X': setSamePointXO(atoi(optarg));
				break;
			case 'H': setSHCC(atoi(optarg));
				break;
			case 'h': setWHCC(atoi(optarg));
				break;
			case 'I': setReplaceIllegals(atoi(optarg));
				break;
			case 'c': setMaxCodonValue(strtoul(optarg, NULL, 0));
				break;
			case 'l': setMaxGenomeLength(strtoul(optarg, NULL, 0));
				break;
			case 'F': setXPName(optarg);
				break;
			case 'S': setRandomSeed(atoi(optarg));
				break;
			default: if(optopt != 0){
					// Unknown option (such as '-x'); add to newargv;
					if(modifyArgv)
						newargv.push_back(argv[optind - 1]);
				}
				else if(optind < argc){
					// Unknown argument (such as '1'); add to newargv;
					if(modifyArgv)
						newargv.push_back(argv[optind]);
					optind++;
				}
		}
		optopt = 0; // Reset marker of last unknown "valid" argument;
	}
	if(modifyArgv){
		// Copy newargv onto argv, and adjust argc;
		for(size_t ii = 0; ii < newargv.size(); ++ii){
			argv[ii + 1] = newargv[ii];
		}
		argc = newargv.size() + 1;
	}
	// Set random seed again, just in case it has not been specified;
	setRandomSeed(getRandomSeed());
}

string qGA::outputParams(const bool strip){
	stringstream output;
	output	<< (strip?"":" ") << "-p" << (strip?"":" ") << getPopSize()
		<< (strip?"":" ") << "-g" << (strip?"":" ") << getMaxGens()
		<< (strip?"":" ") << "-s" << (strip?"":" ") << getSelectorStr()
		<< (strip?"":" ") << "-t" << (strip?"":" ") << getTSizeRatio()
		<< (strip?"":" ") << "-f" << (strip?"":" ") << getFairTournament()
		<< (strip?"":" ") << "-x" << (strip?"":" ") << getXORate()
		<< (strip?"":" ") << "-w" << (strip?"":" ") << getSwapRate()
		<< (strip?"":" ") << "-i" << (strip?"":" ") << getIntMutRate()
		<< (strip?"":" ") << "-a" << (strip?"":" ") << getAvgIntMutRate()
		<< (strip?"":" ") << "-o" << (strip?"":" ") << getExclusiveOps()
		<< (strip?"":" ") << "-r" << (strip?"":" ") << getReplacementStr()
		<< (strip?"":" ") << "-e" << (strip?"":" ") << getElitismRatio()
		<< (strip?"":" ") << "-m" << (strip?"":" ") << getMaximising()
		<< (strip?"":" ") << "-E" << (strip?"":" ") << getEffectiveOps()
		<< (strip?"":" ") << "-M" << (strip?"":" ") << getMarkedXO()
		<< (strip?"":" ") << "-n" << (strip?"":" ") << getXONbrPoints()
		<< (strip?"":" ") << "-X" << (strip?"":" ") << getSamePointXO()
		<< (strip?"":" ") << "-H" << (strip?"":" ") << getSHCC()
		<< (strip?"":" ") << "-h" << (strip?"":" ") << getWHCC()
		<< (strip?"":" ") << "-I" << (strip?"":" ") << getReplaceIllegals()
		<< (strip?"":" ") << "-c" << (strip?"":" ") << getMaxCodonValue()
		<< (strip?"":" ") << "-l" << (strip?"":" ") << getMaxGenomeLength()
		<< (strip?"":" ") << "-F" << (strip?"":" ") << getXPName()
		<< (strip?"":" ") << "-S" << (strip?"":" ") << getRandomSeed();
	return output.str();
}

void qGA::setParams(const qGA &otherPop){
	// Don't set pop size;
	//setPopSize(otherPop.getPopSize());
	setMaxGens(otherPop.getMaxGens());
	setSelector(otherPop.getSelector());
	setTSizeRatio(otherPop.getTSizeRatio());
	setFairTournament(otherPop.getFairTournament());
	setXORate(otherPop.getXORate());
	setSwapRate(otherPop.getSwapRate());
	setIntMutRate(otherPop.getIntMutRate());
	setAvgIntMutRate(otherPop.getAvgIntMutRate());
	setExclusiveOps(otherPop.getExclusiveOps());
	setReplacement(otherPop.getReplacement());
	setElitismRatio(otherPop.getElitismRatio());
	setMaximising(otherPop.getMaximising());
	setEffectiveOps(otherPop.getEffectiveOps());
	setMarkedXO(otherPop.getMarkedXO());
	setXONbrPoints(otherPop.getXONbrPoints());
	setSamePointXO(otherPop.getSamePointXO());
	setSHCC(otherPop.getSHCC());
	setWHCC(otherPop.getWHCC());
	setReplaceIllegals(otherPop.getReplaceIllegals());
	setMaxCodonValue(otherPop.getMaxCodonValue());
	setMaxGenomeLength(otherPop.getMaxGenomeLength());
	setXPName(otherPop.getXPName());
	// Don't set random seed;
	//setRandomSeed(otherPop.getRandomSeed());
}

size_t qGA::getPopSize() const{
	return popSize;
}

void qGA::setPopSize(const size_t &newPopSize){
	if(newPopSize % 2){
		cerr << "Population size must be even.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	popSize = newPopSize;
	resize(popSize);
}

size_t qGA::getMaxGens() const{
	return maxGens;
}

void qGA::setMaxGens(const size_t &newMaxGens){
	maxGens = newMaxGens;
}

size_t qGA::getSelector() const{
	return selector;
}

void qGA::setSelector(const size_t &newSelector){
	selector = newSelector;
}

string qGA::getSelectorStr() const{
	return QGA_STRSELECTOR[selector];
}

void qGA::setSelectorStr(const string &newStr){
	for(size_t ii = 0; ii < QGA_TOTSELECTOR; ++ii){
		if(!strcmp(newStr.c_str(), (QGA_STRSELECTOR[ii]).c_str())){
			selector = ii;
			return;
		}
	}
	cerr << "Unknown selector \"" << newStr << "\".\n";
	cerr << "Execution aborted.\n";
	exit(0);
}

double qGA::getTSizeRatio() const{
	return tSizeRatio;
}

void qGA::setTSizeRatio(const double &newTSizeRatio){
	if(newTSizeRatio >= 0.0 && newTSizeRatio <= 1.0){
		tSizeRatio = newTSizeRatio;
	}
}

bool qGA::getFairTournament() const{
	return fairTournament;
}

void qGA::setFairTournament(const bool &newFairTournament){
	fairTournament = newFairTournament;
}

double qGA::getXORate() const{
	return XORate;
}

void qGA::setXORate(const double &newXORate){
	if(newXORate >= 0.0 && newXORate <= 1.0){
		XORate = newXORate;
	}
}

double qGA::getSwapRate() const{
	return swapRate;
}

void qGA::setSwapRate(const double &newSwapRate){
	if(newSwapRate >= 0.0 && newSwapRate <= 1.0){
		swapRate = newSwapRate;
	}
}

double qGA::getIntMutRate() const{
	return intMutRate;
}

void qGA::setIntMutRate(const double &newIntMutRate){
	if(newIntMutRate >= 0.0 && newIntMutRate <= 1.0){
		intMutRate = newIntMutRate;
	}
}

double qGA::getAvgIntMutRate() const{
	return avgIntMutRate;
}

void qGA::setAvgIntMutRate(const double &newAvgIntMutRate){
	if(newAvgIntMutRate >= 0.0){
		avgIntMutRate = newAvgIntMutRate;
	}
}

bool qGA::getExclusiveOps() const{
	return exclusiveOps;
}

void qGA::setExclusiveOps(const bool newExclusiveOps){
	exclusiveOps = newExclusiveOps;
}

size_t qGA::getReplacement() const{
	return replacement;
}

void qGA::setReplacement(const size_t &newReplacement){
	replacement = newReplacement;
}

string qGA::getReplacementStr() const{
	return QGA_STRREPLACEMENT[replacement];
}

void qGA::setReplacementStr(const string &newStr){
	for(size_t ii = 0; ii < QGA_TOTREPLACEMENT; ++ii){
		if(!strcmp(newStr.c_str(), (QGA_STRREPLACEMENT[ii]).c_str())){
			replacement = ii;
			return;
		}
	}
	cerr << "Unknown replacement \"" << newStr << "\".\n";
	cerr << "Execution aborted.\n";
	exit(0);
}

double qGA::getElitismRatio() const{
	return elitismRatio;
}

void qGA::setElitismRatio(const double &newElitismRatio){
	if(newElitismRatio >= 0.0 && newElitismRatio <= 1.0){
		elitismRatio = newElitismRatio;
	}
}

bool qGA::getMaximising() const{
	return maximising;
}

void qGA::setMaximising(const bool &newMaximising){
	maximising = newMaximising;
}

bool qGA::getEffectiveOps() const{
	return effectiveOps;
}

void qGA::setEffectiveOps(const bool newEffectiveOps){
	effectiveOps = newEffectiveOps;
}

bool qGA::getMarkedXO() const{
	return markedXO;
}

void qGA::setMarkedXO(const bool newMarkedXO){
	markedXO = newMarkedXO;
}

size_t qGA::getXONbrPoints() const{
	return XONbrPoints;
}

void qGA::setXONbrPoints(const size_t &newXONbrPoints){
	if(newXONbrPoints <= 2){
		XONbrPoints = newXONbrPoints;
	}
	if((shcc || whcc) && XONbrPoints != 1){
		cerr << "Headless chicken crossover with more than"
			<< " 1 crossover point not implemented yet.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
}

bool qGA::getSamePointXO() const{
	return samePointXO;
}

void qGA::setSamePointXO(const bool &newSamePointXO){
	samePointXO = newSamePointXO;
}

bool qGA::getSHCC() const{
	return shcc;
}

void qGA::setSHCC(const bool &newSHCC){
	shcc = newSHCC;
	if(shcc && whcc){
		cerr << "Strong and weak headless chicken crossover cannot be both active.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	if((shcc || whcc) && XONbrPoints != 1){
		cerr << "Headless chicken crossover with more than"
			<< " 1 crossover point not implemented yet.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
}

bool qGA::getWHCC() const{
	return whcc;
}

void qGA::setWHCC(const bool &newWHCC){
	whcc = newWHCC;
	if(shcc && whcc){
		cerr << "Strong and weak headless chicken crossover cannot be both active.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	if((shcc || whcc) && XONbrPoints != 1){
		cerr << "Headless chicken crossover with more than"
			<< " 1 crossover point not implemented yet.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
}

bool qGA::getReplaceIllegals() const{
	return replaceIllegals;
}

void qGA::setReplaceIllegals(const bool &newReplaceIllegals){
	replaceIllegals = newReplaceIllegals;
}

string qGA::getXPName() const{
	return XPName;
}

void qGA::setXPName(const string &newXPName){
	XPName = newXPName;
}

size_t qGA::getMaxCodonValue() const{
	return maxCodonValue;
}

void qGA::setMaxCodonValue(const size_t &newMaxCodonValue){
	maxCodonValue = newMaxCodonValue;
}

size_t qGA::getMaxGenomeLength() const{
	return maxGenomeLength;
}

void qGA::setMaxGenomeLength(const size_t &newMaxGenomeLength){
	maxGenomeLength = newMaxGenomeLength;
}

int qGA::getRandomSeed() const{
	return randomSeed;
}

void qGA::setRandomSeed(const int &newRandomSeed){
	randomSeed = newRandomSeed;
	srand(randomSeed);
}

///////////////////////////////////////////////////////////////////////////////
// Selection
///////////////////////////////////////////////////////////////////////////////

int qGA::tournamentSelect(){
	assert(size());
	assert(getTSizeRatio() >= 0.0);
	// If not keeping picks, reset previous picks;
	if(!getFairTournament()){
		tournamentPicks.clear();
	}
	// Set tournament size;
	size_t tSize = getTSizeRatio() * size();
	if(tSize < 2){
		tSize = 2;
	}
	if(tSize >= size()){
		cerr << "Population size (" << size()
			<< ") too small for tournament size (" << tSize << ").\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	// If not enough picks left, reset to contain whole population;
	if(tournamentPicks.size() < tSize){
		tournamentPicks.resize(size());
		for(size_t ii = 0; ii < tournamentPicks.size(); ++ii){
			tournamentPicks[ii] = ii;
		}
	}
	// Randomly pick tSize elements from possible picks
	// and add them to competitors;
	vector<size_t> competitors;
	while(competitors.size() < tSize && tournamentPicks.size()){
		size_t pick = rand() % tournamentPicks.size();
		competitors.push_back(tournamentPicks.at(pick));
		tournamentPicks.erase(tournamentPicks.begin() + pick);
	}
	// Now pick best fitness;
	size_t gIndexBest = competitors[rand() % competitors.size()];
	for(size_t cIndex = 0; cIndex < competitors.size(); ++cIndex){
		if(getMaximising() && at(competitors[cIndex]).fitness >
			at(gIndexBest).fitness){
			gIndexBest = competitors[cIndex];
		}
		else if(!getMaximising() && at(competitors[cIndex]).fitness <
			at(gIndexBest).fitness){
			gIndexBest = competitors[cIndex];
		}
	}
	return gIndexBest;
}

int qGA::rankSelect(){
	assert(size());
	// Sort population;
	if(getMaximising()){
		sort(begin(), end(), genotypeFitGTCmp);
	}
	else{
		sort(begin(), end(), genotypeFitLTCmp);
	}
	// Total size of ranks;
	size_t rankSize = size() * (size() + 1) / 2;
	// Randomly pick a rank selector in range;
	size_t rank = rand() % rankSize;
	// Now map rank onto a population index;
	// Use (floored) positive root of quadratic formula;
	size_t index = static_cast<size_t>
		(floor((-1 + sqrt(1.0 + 4 * rank * 2)) / 2));
	assert(index < size());
	return index;
}

///////////////////////////////////////////////////////////////////////////////
// Generate
///////////////////////////////////////////////////////////////////////////////

void qGA::generateOffspring(qGA &offspring){
	// Clear previous tournament picks, just in case;
	tournamentPicks.clear();
	// Clear stats keepers;
	resetStats();
	// Clear input offspring pop;
	offspring.clear();
	// Copy parent parameters;
	offspring.setParams(*this);
	// Grab latest used ID;
	size_t latestID = size();
	// SELECTION;
	while(offspring.size() < size()){
		// Select two parents;
		size_t pInd1, pInd2;
		do switch(getSelector()){
			case QGA_TOURNAMENT: pInd1 = tournamentSelect();
					pInd2 = tournamentSelect();
					break;
			case QGA_RANK:	pInd1 = rankSelect();
					pInd2 = rankSelect();
					break;
			default:	cerr << "Unknown selector.\nExecution aborted.";
					exit(0);
		} while(pInd1 == pInd2);
		// Push them onto offspring;
		offspring.push_back(at(pInd1));
		offspring.back().ID = ++latestID;
		offspring.back().ancestors.push_back(make_pair(at(pInd1).ID, 0));
		offspring.push_back(at(pInd2));
		offspring.back().ID = ++latestID;
		offspring.back().ancestors.push_back(make_pair(at(pInd2).ID, 0));
	}
	// GENETIC OPERATORS;
	for(size_t index = 0; index < offspring.size() - 1; index += 2){
		bool opApplied = false;// Flag if any operator has been applied;
		// Apply crossover;
		size_t ind1 = index;
		size_t ind2 = index + 1;
		bool valid1 = offspring[ind1].valid;
		bool valid2 = offspring[ind2].valid;
		size_t events = 0;
		if(getMarkedXO()){
			// Try normal XO;
			if(!(events = offspring.doMarkedCrossover(ind1, ind2))){
				// Normal XO failed, try swap XO;
				if((events = offspring.doMarkedCrossover(ind1, ind1))){
					offspring[ind1].ancestors.back().second =
						offspring[ind1].ancestors.back().first;
					swapEvents += events;
				}
				if((events = offspring.doMarkedCrossover(ind2, ind2))){
					offspring[ind2].ancestors.back().second =
						offspring[ind2].ancestors.back().first;
					swapEvents += events;
				}
			}
			else{
				XOEvents += events;
				offspring[ind1].ancestors.back().second =
					offspring[ind2].ancestors.back().first;
				offspring[ind2].ancestors.back().second =
					offspring[ind1].ancestors.back().first;
			}
		}
		else{
			if((events = offspring.doCrossover(ind1, ind2))){
				XOEvents += events;
				offspring[ind1].ancestors.back().second =
					offspring[ind2].ancestors.back().first;
				offspring[ind2].ancestors.back().second =
					offspring[ind1].ancestors.back().first;
			}
		}
		opApplied = events;
		// Keep track of invalid xo parents (i.e. necrophilia)
		if(opApplied) necrophiles += (!valid1 + !valid2);
		// Apply int mutation;
		if(!getExclusiveOps() || !opApplied){
			events  = offspring.doIntMutation(ind1);
			events += offspring.doIntMutation(ind2);
			intMutEvents += events;
		}
		opApplied = events;
		// Apply avg int mutation;
		if(!getExclusiveOps() || !opApplied){
			events  = offspring.doAvgIntMutation(ind1);
			events += offspring.doAvgIntMutation(ind2);
			avgIntMutEvents += events;
		}
	}
}

///////////////////////////////////////////////////////////////////////////////
// Replacement
///////////////////////////////////////////////////////////////////////////////

void qGA::replace(const qGA &offspring){
	invalidOffspring = 0;
	switch(getReplacement()){
		case QGA_GENERATIONAL:
				genReplacement(offspring);
				break;
		case QGA_SSTATE:
				ssReplacement(offspring);
				break;
		default:	cerr << "Unknown replacement.\nExecution aborted.";
				exit(0);
	}
}

void qGA::genReplacement(const qGA &offspring, size_t elites){
	// Ensure not too many elites;
	assert(elites <= size());
	// Add legal offspring to local offspring;
	vector<qGAIndividual> localOffspring;
	for(size_t ii = 0; ii < offspring.size(); ++ii){
		if(offspring[ii].valid){
			localOffspring.push_back(offspring[ii]);
		}
		else{
			invalidOffspring++;
			if(!getReplaceIllegals()){
				localOffspring.push_back(offspring[ii]);
			}
		}
	}
	// Sort both populations;
	if(getMaximising()){
		sort(begin(), end(), genotypeFitGTCmp);
		sort(localOffspring.begin(), localOffspring.end(), genotypeFitGTCmp);
	}
	else{
		sort(begin(), end(), genotypeFitLTCmp);
		sort(localOffspring.begin(), localOffspring.end(), genotypeFitLTCmp);
	}
	// Calculate number of members to keep;
	size_t toKeep = elites + max(int(size() - elites - localOffspring.size()), int(0));
	// Calculate number of offspring to keep;
	size_t offToKeep = size() - toKeep;
	// Clear all except elites and members needed to make up size;
	erase(begin() + toKeep, end());
	// Clear extra offspring;
	localOffspring.erase(localOffspring.begin() + offToKeep, localOffspring.end());
	// Append offspring to parents;
	insert(end(), localOffspring.begin(), localOffspring.end());
	// Finally, reset tournamentPicks as population has changed;
	tournamentPicks.clear();
}

void qGA::genReplacement(const qGA &offspring){
	assert(getElitismRatio() >= 0.0 && getElitismRatio() < 1.0);
	// Call main method, with percentage converted to size;
	genReplacement(offspring, static_cast<size_t>(size() * getElitismRatio()));
}

void qGA::ssReplacement(const qGA &offspring){
	if(!offspring.size()){
		return;
	}
	// Add legal offspring to local offspring;
	vector<qGAIndividual> localOffspring;
	for(size_t ii = 0; ii < offspring.size(); ++ii){
		if(offspring[ii].valid){
			localOffspring.push_back(offspring[ii]);
		}
		else{
			invalidOffspring++;
			if(!getReplaceIllegals()){
				localOffspring.push_back(offspring[ii]);
			}
		}
	}
	// Save current population size;
	size_t savedPopSize = size();
	// Add local offspring copies to population;
	insert(end(), localOffspring.begin(), localOffspring.end());
	// Sort population;
	if(getMaximising()){
		sort(begin(), end(), genotypeFitGTCmp);
	}
	else{
		sort(begin(), end(), genotypeFitLTCmp);
	}
	// Cull to saved size;
	erase(begin() + savedPopSize, end());
	// Finally, reset tournamentPicks as population has changed;
	tournamentPicks.clear();
}

///////////////////////////////////////////////////////////////////////////////
// Genetic Operators
///////////////////////////////////////////////////////////////////////////////

bool qGA::doCrossover(const size_t ind1, const size_t ind2){
	// Create crossover sites vectors, with all codon intervals;
	vector<size_t> xoSites1, xoSites2;
	size_t lastPoint;
	if(!effectiveOps){
		lastPoint = at(ind1).genotype.size();
	}
	else{
		lastPoint = at(ind1).effectiveSize;
	}
	for(size_t ii = 1; ii <= lastPoint; ++ii){
		xoSites1.push_back(ii);
	}
	if(!effectiveOps){
		lastPoint = at(ind2).genotype.size();
	}
	else{
		lastPoint = at(ind2).effectiveSize;
	}
	for(size_t ii = 1; ii <= lastPoint; ++ii){
		xoSites2.push_back(ii);
	}
	return doCrossover(ind1, ind2, xoSites1, xoSites2);
}

bool qGA::doMarkedCrossover(const size_t ind1, const size_t ind2){
	// Call crossover method with marks vectors;
	return doCrossover(ind1, ind2, at(ind1).xoSites, at(ind2).xoSites);
}

bool qGA::doCrossover(const size_t ind1, const size_t ind2,
	const vector<size_t> &xoSites1, const vector<size_t> &xoSites2){
	// Crossover or not?
	if((ind1 != ind2 && rand() / (RAND_MAX + 1.0) > getXORate())
		|| (ind1 == ind2 && rand() / (RAND_MAX + 1.0) > getSwapRate())){
		return false;
	}
	// Check validity of indexes;
	if(ind1 >= size() || ind2 >= size()){
		return false;
	}
	// Check if both parents have at least two codons;
	if(at(ind1).genotype.size() < 2 || at(ind2).genotype.size() < 2){
		return false;
	}
	// Check if it is possible to crossover and keep genome lengths valid;
	if(at(ind1).genotype.size() + at(ind2).genotype.size() > getMaxGenomeLength() * 2){
		return false;
	}
	// If same point on both individuals, sizes must be valid already;
	if(samePointXO && (at(ind1).genotype.size() > getMaxGenomeLength()
		|| at(ind2).genotype.size() > getMaxGenomeLength())){
		return false;
	}
	// If same point on both individuals, ensure size of points vector is same;
	if(samePointXO){
		assert(xoSites1.size() == xoSites2.size());
	}
	// Set size of each individual;
	size_t ind1Size = at(ind1).genotype.size();
	size_t ind2Size = at(ind2).genotype.size();
	if(effectiveOps){
		if(at(ind1).effectiveSize <= ind1Size){
			ind1Size = at(ind1).effectiveSize;
		}
		if(at(ind2).effectiveSize <= ind2Size){
			ind2Size = at(ind2).effectiveSize;
		}
	}
	// Check validity of crossover points;
	size_t pointsRemaining = XONbrPoints;
	if(ind1 == ind2){
		// Special case: swap mutation;
		// Choose 4 points if possible;
		pointsRemaining = 4;
	}
	if(xoSites1.size() < pointsRemaining || xoSites2.size() < pointsRemaining){
		return false;
	}
	// Check all xo points are valid;
	for(size_t ii = 0; ii < xoSites1.size(); ++ii){
		if(xoSites1[ii] > ind1Size){
			return false;
		}
	}
	for(size_t ii = 0; ii < xoSites2.size(); ++ii){
		if(xoSites2[ii] > ind2Size){
			return false;
		}
	}
	// Choose crossover points;
	vector<size_t> points1, points2;
	while(pointsRemaining){
		pointsRemaining--;
		size_t chosenPoint;
		do{
			if(!points1.size()){
				chosenPoint = rand() % (xoSites1.size() -
					pointsRemaining);
			}
			else{
				chosenPoint = points1.back() + 1 + rand() % (xoSites1.size() -
					pointsRemaining - points1.back() - 1);
			}
		} while (xoSites1[chosenPoint] > ind1Size);
		points1.push_back(chosenPoint);
		if(!samePointXO) do{
			if(!points2.size()){
				chosenPoint = rand() % (xoSites2.size() -
					pointsRemaining);
			}
			else{
				chosenPoint = points2.back() + 1 + rand() % (xoSites2.size() -
					pointsRemaining - points2.back() - 1);
			}
		} while (xoSites2[chosenPoint] > ind2Size);
		points2.push_back(chosenPoint);
	}
	// Translate XO points into real chromosome coordinates;
	// Also, use loop to flag valid state of each individual, if any
	// XO point is in effective part;
	for(size_t ii = 0; ii < points1.size(); ++ii){
		points1[ii] = xoSites1[points1[ii]];
		at(ind1).valid = at(ind1).valid &&
			points1[ii] > at(ind1).effectiveSize;
	}
	for(size_t ii = 0; ii < points2.size(); ++ii){
		points2[ii] = xoSites2[points2[ii]];
		at(ind2).valid = at(ind2).valid &&
			points2[ii] > at(ind2).effectiveSize;
	}
	/*
		//Debugging info;
		cout << "G1(" << at(ind1).genotype.size() << "):";
		for(size_t ii = 0; ii < at(ind1).genotype.size(); ++ii){
			for(size_t jj = 0; jj < points1.size(); ++jj)
				if(points1[jj] == ii) cout << " .";
			cout << " " << at(ind1).genotype[ii];
		}
		if(points1[points1.size() - 1] == at(ind1).genotype.size()) cout << " .";
		cout << endl;
		cout << "XO1:";
		for(size_t ii = 0; ii < points1.size(); ++ii){
			cout << " " << points1[ii];
		}
		cout << endl;
		cout << "G2(" << at(ind2).genotype.size() << "):";
		for(size_t ii = 0; ii < at(ind2).genotype.size(); ++ii){
			for(size_t jj = 0; jj < points2.size(); ++jj)
				if(points2[jj] == ii) cout << " .";
			cout << " " << at(ind2).genotype[ii];
		}
		if(points2[points2.size() - 1] == at(ind2).genotype.size()) cout << " .";
		cout << endl;
		cout << "XO2:";
		for(size_t ii = 0; ii < points2.size(); ++ii){
			cout << " " << points2[ii];
		}
		cout << endl;
	*/
	// Now transform them onto offsets (for n-point crossover only);
	if(ind1 != ind2){
		for(size_t ii = points1.size(); ii > 1; --ii){
			assert(points1[ii - 1] > points1[ii - 2]);
			points1[ii - 1] -= points1[ii - 2];
		}
		for(size_t ii = points2.size(); ii > 1; --ii){
			assert(points2[ii - 1] > points2[ii - 2]);
			points2[ii - 1] -= points2[ii - 2];
		}
	}
	// Exchange genetic material between individuals;
	size_t begin1, begin2;
	begin1 = begin2 = 0;
	bool invert = false; // Need to invert points after every exchange;
	// Make a safe opy of the current individuals, in case maxGenomeLength is violated;
	qGAIndividual safe1(at(ind1)), safe2(at(ind2));
	// FIXME: Cheap, but calculating the possible xo points to ensure maxGenomeLength isn't violated is really hard!
	while(points1.size()){
		if(ind1 == ind2){
			// Swap mutation;
			Genotype newInd1;
			// Copy genes up to 1st points;
			newInd1.insert(newInd1.end(), at(ind1).genotype.begin(),
				at(ind1).genotype.begin() + points1[0]);
			// Copy section between 3rd and 4th points;
			newInd1.insert(newInd1.end(), at(ind1).genotype.begin() + points1[2],
				at(ind1).genotype.begin() + points1[3]);
			// Copy section between 2nd and 3rd points;
			newInd1.insert(newInd1.end(), at(ind1).genotype.begin() + points1[1],
				at(ind1).genotype.begin() + points1[2]);
			// Copy section between 1st and 2nd points;
			newInd1.insert(newInd1.end(), at(ind1).genotype.begin() + points1[0],
				at(ind1).genotype.begin() + points1[1]);
			// Copy section from 4th point until the end;
			newInd1.insert(newInd1.end(), at(ind1).genotype.begin() + points1[3],
				at(ind1).genotype.end());
			// Finally, copy back into genotype;
			at(ind1).genotype = newInd1;
			break;
		}
		else if(shcc || whcc){
			size_t p1 = points1[0];
			size_t p2 = points2[0];
			size_t length1stHalfInd1 = p1;
			size_t length1stHalfInd2 = p2;
			size_t length2ndHalfInd1 = at(ind1).genotype.size() - p1;
			size_t length2ndHalfInd2 = at(ind2).genotype.size() - p2;
			// 1st individual;
			if(shcc){
				// Erase 2nd half of ind1;
				at(ind1).genotype.erase(at(ind1).genotype.begin() + p1,
					at(ind1).genotype.end());
				// Insert random codons onto ind1, making up length of 2nd half of ind2;
				for(size_t ii = 0; ii < length2ndHalfInd2; ++ii)
					at(ind1).genotype.push_back(rand() % getMaxCodonValue());
			}
			else{
				// Erase 1st half of ind1;
				at(ind1).genotype.erase(at(ind1).genotype.begin(),
					at(ind1).genotype.begin() + p1);
				// Insert random codons onto ind1, making up length of 1st half of ind2;
				for(size_t ii = 0; ii < length1stHalfInd2; ++ii)
					at(ind1).genotype.insert(at(ind1).genotype.begin(), rand() % getMaxCodonValue());
			}
			// 2nd individual;
			if(shcc){
				// Erase 2nd half of ind2;
				at(ind2).genotype.erase(at(ind2).genotype.begin() + p2,
					at(ind2).genotype.end());
				// Insert random codons onto ind2, making up length of 2nd half of ind1;
				for(size_t ii = 0; ii < length2ndHalfInd1; ++ii)
					at(ind2).genotype.push_back(rand() % getMaxCodonValue());
			}
			else{
				// Erase 1st half of ind2;
				at(ind2).genotype.erase(at(ind2).genotype.begin(),
					at(ind2).genotype.begin() + p2);
				// Insert random codons onto ind2, making up length of 1st half of ind1;
				for(size_t ii = 0; ii < length1stHalfInd1; ++ii)
					at(ind2).genotype.insert(at(ind2).genotype.begin(), rand() % getMaxCodonValue());
			}
			break;
		}
		else{
			// Standard n-point;
			size_t p1 = points1[0];
			size_t p2 = points2[0];
			if(invert){
				size_t p3 = p1;
				p1 = p2;
				p2 = p3;
			}
			p1 += begin1;
			p2 += begin2;
			invert = !invert;
			// Save 2nd half of ind1;
			vector<int> savedHalfInd1;
			savedHalfInd1.insert(savedHalfInd1.begin(),
				at(ind1).genotype.begin() + p1, at(ind1).genotype.end());
			// Erase 2nd half of ind1;
			at(ind1).genotype.erase(at(ind1).genotype.begin() + p1,
				at(ind1).genotype.end());
			// Copy 2nd half of ind2 onto ind1;
			at(ind1).genotype.insert(at(ind1).genotype.end(),
				at(ind2).genotype.begin() + p2, at(ind2).genotype.end());
			// Erase 2nd half of ind2;
			at(ind2).genotype.erase(at(ind2).genotype.begin() + p2,
				at(ind2).genotype.end());
			// Copy saved 2nd half of ind1 onto ind2;
			at(ind2).genotype.insert(at(ind2).genotype.end(),
				savedHalfInd1.begin(), savedHalfInd1.end());
			points1.erase(points1.begin());
			points2.erase(points2.begin());
			begin1 = p1;
			begin2 = p2;
		}
	}
	// Check sizes are correct;
	if(at(ind1).genotype.size() > getMaxGenomeLength()
		|| at(ind2).genotype.size() > getMaxGenomeLength()){
		at(ind1) = safe1;
		at(ind2) = safe2;
		return false;
	}
	// Finally, effective size is destroyed if there were changes, set to max;
	if(at(ind1).valid) at(ind1).effectiveSize = at(ind1).genotype.size();
	if(at(ind2).valid) at(ind2).effectiveSize = at(ind2).genotype.size();
	/* Debugging info;
		cout << "N1:";
		for(size_t ii = 0; ii < at(ind1).genotype.size(); ++ii){
		cout << " " << at(ind1).genotype[ii];
		}
		cout << endl;
		cout << "N2:";
		for(size_t ii = 0; ii < at(ind2).genotype.size(); ++ii){
		cout << " " << at(ind2).genotype[ii];
		}
		cout << endl;
	*/
	return true;
}

size_t qGA::doIntMutation(const size_t ind){
	// Check validity of index;
	if(ind >= size()){
		return 0;
	}
	// Check if individual has at least one codon;
	if(at(ind).genotype.size() < 1){
		return 0;
	}
	// Choose length to mutate; XO sets effective size to full
	// genome size if there were changes;
	size_t lastPoint;
	if(!effectiveOps){
		lastPoint = at(ind).genotype.size();
	}
	else{
		lastPoint = min(at(ind).genotype.size(), at(ind).effectiveSize);
	}
	// Mutate each integer with intMutRate;
	size_t mutated = 0;
	for(size_t ii = 0; ii < lastPoint; ++ii){
		if(rand() / (RAND_MAX + 1.0) > intMutRate){
			continue;
		}
		at(ind).genotype.at(ii) = static_cast<size_t>
			//(rand() / (RAND_MAX + 1.0) * (getMaxCodonValue()));
			(rand() % getMaxCodonValue());
		mutated++;
		// Invalidate if still valid and mutation in effective zone;
		at(ind).valid = at(ind).valid &&
			ii > at(ind).effectiveSize;
	}
	return mutated;
}

size_t qGA::doAvgIntMutation(const size_t ind){
	// Check validity of index;
	if(ind >= size()){
		return 0;
	}
	// Check if individual has at least one codon;
	if(at(ind).genotype.size() < 1){
		return 0;
	}
	// Choose length to mutate; XO sets effective size to full
	// genome size if there were changes;
	size_t lastPoint;
	if(!effectiveOps){
		lastPoint = at(ind).genotype.size();
	}
	else{
		lastPoint = min(at(ind).genotype.size(), at(ind).effectiveSize);
	}
	// Calculate real mutation, based on length of individual;
	//double realMutRate = avgIntMutRate / at(ind).genotype.size();
	double realMutRate = avgIntMutRate / lastPoint;
	// Mutate each integer with probability realMutRate;
	size_t mutated = 0;
	for(size_t ii = 0; ii < lastPoint; ++ii){
		if(rand() / (RAND_MAX + 1.0) > realMutRate){
			continue;
		}
		at(ind).genotype.at(ii) = static_cast<size_t>
			//(rand() / (RAND_MAX + 1.0) * (getMaxCodonValue()));
			(rand() % getMaxCodonValue());
		mutated++;
		// Invalidate if still valid and mutation in effective zone;
		at(ind).valid = at(ind).valid &&
			ii > at(ind).effectiveSize;
	}
	return mutated;
}

///////////////////////////////////////////////////////////////////////////////
// Stats
///////////////////////////////////////////////////////////////////////////////

double qGA::getSumFit() const{
	double sum = 0.0;
	for(size_t ii = 0; ii < size(); ++ii){
		sum += at(ii).fitness;
	}
	return sum;
}

double qGA::getAvgFit() const{
	assert(size());
	return getSumFit() / size();
}

size_t qGA::getMaxFitIndex() const{
	assert(size());
	double highestFit = 0.0;
	size_t index = 0;
	for(size_t ii = 0; ii < size(); ++ii){
		if(at(ii).fitness > highestFit){
			highestFit = at(ii).fitness;
			index = ii;
		}
	}
	return index;
}

size_t qGA::getMinFitIndex() const{
	assert(size());
	double lowestFit = FLT_MAX;
	size_t index = 0;
	for(size_t ii = 0; ii < size(); ++ii){
		if(at(ii).fitness < lowestFit){
			lowestFit = at(ii).fitness;
			index = ii;
		}
	}
	return index;
}

size_t qGA::getBestFitIndex() const{
	if(getMaximising()){
		return getMaxFitIndex();
	}
	return getMinFitIndex();
}

double qGA::getBestFit() const{
	return at(getBestFitIndex()).fitness;
}

size_t qGA::getWorstFitIndex() const{
	if(getMaximising()){
		return getMinFitIndex();
	}
	return getMaxFitIndex();
}

double qGA::getWorstFit() const{
	return at(getWorstFitIndex()).fitness;
}

double qGA::getAvgSize() const{
	assert(size());
	double length = 0.0;
	for(size_t ii = 0; ii < size(); ++ii){
		length += at(ii).genotype.size();
	}
	return length / size();
}

size_t qGA::getMaxSizeIndex() const{
	size_t maxSize = 0;
	size_t index = 0;
	for(size_t ii = 0; ii < size(); ++ii){
		if(at(ii).genotype.size() > maxSize){
			maxSize = at(ii).genotype.size();
			index = ii;
		}
	}
	return index;
}

size_t qGA::getMinSizeIndex() const{
	size_t minSize = numeric_limits<size_t>::max();
	size_t index = 0;
	for(size_t ii = 0; ii < size(); ++ii){
		if(at(ii).genotype.size() < minSize){
			minSize = at(ii).genotype.size();
			index = ii;
		}
	}
	return index;
}

double qGA::getAvgEffSize() const{
	assert(size());
	double length = 0.0;
	for(size_t ii = 0; ii < size(); ++ii){
		length += at(ii).effectiveSize;
	}
	return length / size();
}

size_t qGA::getMaxEffSizeIndex() const{
	size_t maxEffSize = 0;
	size_t index = 0;
	for(size_t ii = 0; ii < size(); ++ii){
		if(at(ii).effectiveSize > maxEffSize){
			maxEffSize = at(ii).effectiveSize;
			index = ii;
		}
	}
	return index;
}

size_t qGA::getMinEffSizeIndex() const{
	size_t minEffSize = numeric_limits<size_t>::max();
	size_t index = 0;
	for(size_t ii = 0; ii < size(); ++ii){
		if(at(ii).effectiveSize < minEffSize){
			minEffSize = at(ii).effectiveSize;
			index = ii;
		}
	}
	return index;
}

void qGA::outputStats(const size_t &gens, const size_t &evals){
	// Standard stats;
	ofstream f;
	f.open((getXPName() + "-FIT-gen_obj_best_avg_worst.dat").c_str(), ios::app);
	if(!f.is_open()){
		cerr << "Couldn't append fit stats file.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	f << gens
		<< "\t" << evals
		<< "\t" << getBestFit()
		<< "\t" << getAvgFit()
		<< "\t" << getWorstFit() << "\n";
	f.close();
	// Operator stats;
	f.open((getXPName() + "-OPS-gen_xo_swap_int_avgint.dat").c_str(), ios::app);
	if(!f.is_open()){
		cerr << "Couldn't append ops stats file.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	f << gens
		<< "\t" << XOEvents
		<< "\t" << swapEvents
		<< "\t" << intMutEvents
		<< "\t" << avgIntMutEvents << "\n";
	f.close();
	// Size stats;
	f.open((getXPName() + "-SIZE-gen_max_avg_min_effmax_effavg_effmin.dat").c_str(), ios::app);
	if(!f.is_open()){
		cerr << "Couldn't append size stats file.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	f << gens
		<< "\t" << at(getMaxSizeIndex()).genotype.size()
		<< "\t" << getAvgSize()
		<< "\t" << at(getMinSizeIndex()).genotype.size()
		<< "\t" << at(getMaxEffSizeIndex()).effectiveSize
		<< "\t" << getAvgEffSize()
		<< "\t" << at(getMinEffSizeIndex()).effectiveSize
		<< "\n";
	f.close();
	// Valid stats;
	// Check if invalids have been counted (1st gen probably not);
	if(invalidOffspring == -1){
		invalidOffspring = 0;
		for(size_t ii = 0; ii < size(); ++ii) invalidOffspring += (!at(ii).valid);
	}
	f.open((getXPName() + "-VALID-gen_invalid.dat").c_str(), ios::app);
	if(!f.is_open()){
		cerr << "Couldn't append valids stats file.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	f << gens
		<< "\t" << invalidOffspring
		<< "\n";
	f.close();
	// Necrophilia stats;
	f.open((getXPName() + "-NECROPHILIA-gen_necrophiles.dat").c_str(), ios::app);
	if(!f.is_open()){
		cerr << "Couldn't append necrophilia stats file.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	f << gens
		<< "\t" << necrophiles
		<< "\n";
	f.close();
	// Extra stats;
	set<string> extraLabels;
	// Go through all individuals, collect existing extra labels;
	for(size_t pi = 0; pi < size(); ++pi){
		for(map<string, double>::iterator ei = at(pi).extra.begin();
			ei != at(pi).extra.end(); ++ei){
			extraLabels.insert(ei->first);
		}
	}
	// Output stats for each label;
	for(set<string>::iterator label = extraLabels.begin();
		label != extraLabels.end(); ++label){
		f.open((getXPName() + "-" + *label +
			"-gen_max_avg_min_totalvalues_totalsum.dat").c_str(), ios::app);
		if(!f.is_open()){
			cerr << "Couldn't append extra stats file.\n"
				<< "Execution aborted.\n";
			exit(0);
		}
		// Find all appropriate values;
		double maxVal = FLT_MIN;
		double minVal = FLT_MAX;
		size_t totalVal = 0;
		double totalSum = 0;
		for(size_t pi = 0; pi < size(); ++pi){
			// Check if individual has current label;
			map<string, double>::iterator ei = at(pi).extra.find(*label);
			if(ei != at(pi).extra.end()){
				if(ei->second > maxVal)
					maxVal = ei->second;
				if(ei->second < minVal)
					minVal = ei->second;
				totalVal++;
				totalSum+=ei->second;
			}
		}
		// Output values;
		f << gens
			<< "\t" << maxVal
			<< "\t" << (totalVal > 0?totalSum/totalVal:0.0)
			<< "\t" << minVal
			<< "\t" << totalVal
			<< "\t" << totalSum
			<< "\n";
		f.close();
	}
	// Best individual;
	stringstream bestName;
	stringstream bestValues;
	bestName << getXPName() << "-BEST-gen";
	bestValues << gens;
	for(set<string>::iterator label = extraLabels.begin();
		label != extraLabels.end(); ++label){
		bestName << "_" << *label;
		bestValues << "\t" << at(getBestFitIndex()).extra.find(*label)->second;
	}
	bestName << ".dat";
	f.open(bestName.str().c_str(), ios::app);
	f << bestValues.str() << "\n";
	f.close();
}

void qGA::resetStats(){
	XOEvents = swapEvents = intMutEvents = avgIntMutEvents =
		invalidOffspring = necrophiles = 0;
}

bool genotypeFitGTCmp(const qGAIndividual &g1, const qGAIndividual &g2){
	return g1.fitness > g2.fitness;
}

bool genotypeFitLTCmp(const qGAIndividual &g1, const qGAIndividual &g2){
	return g1.fitness < g2.fitness;
}

#endif

