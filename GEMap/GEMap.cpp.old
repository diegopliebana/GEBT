#ifndef GEMAP_CPP
#define GEMAP_CPP

#include "GEMap.h"
#include <string>
#include <vector>
#include <stack>
#include <sstream>
#include <iostream>
#include <fstream>
#include <algorithm>
#include <cassert>
#include <climits>
#include <cstdlib>
#include <cstring>
#include <unistd.h>

#define DEBUGGING 0

using namespace std;

// Primes for hash table;
#define MAXPRIMES 500
size_t hashPrimes[MAXPRIMES] = {
	   2,     3,     5,     7,    11,    13,    17,    19,    23,    29,
	  31,    37,    41,    43,    47,    53,    59,    61,    67,    71,
	  73,    79,    83,    89,    97,   101,   103,   107,   109,   113,
	 127,   131,   137,   139,   149,   151,   157,   163,   167,   173,
	 179,   181,   191,   193,   197,   199,   211,   223,   227,   229,
	 233,   239,   241,   251,   257,   263,   269,   271,   277,   281,
	 283,   293,   307,   311,   313,   317,   331,   337,   347,   349,
	 353,   359,   367,   373,   379,   383,   389,   397,   401,   409,
	 419,   421,   431,   433,   439,   443,   449,   457,   461,   463,
	 467,   479,   487,   491,   499,   503,   509,   521,   523,   541,
	 547,   557,   563,   569,   571,   577,   587,   593,   599,   601,
	 607,   613,   617,   619,   631,   641,   643,   647,   653,   659,
	 661,   673,   677,   683,   691,   701,   709,   719,   727,   733,
	 739,   743,   751,   757,   761,   769,   773,   787,   797,   809,
	 811,   821,   823,   827,   829,   839,   853,   857,   859,   863,
	 877,   881,   883,   887,   907,   911,   919,   929,   937,   941,
	 947,   953,   967,   971,   977,   983,   991,   997,  1009,  1013,
	1019,  1021,  1031,  1033,  1039,  1049,  1051,  1061,  1063,  1069,
	1087,  1091,  1093,  1097,  1103,  1109,  1117,  1123,  1129,  1151,
	1153,  1163,  1171,  1181,  1187,  1193,  1201,  1213,  1217,  1223,
	1229,  1231,  1237,  1249,  1259,  1277,  1279,  1283,  1289,  1291,
	1297,  1301,  1303,  1307,  1319,  1321,  1327,  1361,  1367,  1373,
	1381,  1399,  1409,  1423,  1427,  1429,  1433,  1439,  1447,  1451,
	1453,  1459,  1471,  1481,  1483,  1487,  1489,  1493,  1499,  1511,
	1523,  1531,  1543,  1549,  1553,  1559,  1567,  1571,  1579,  1583,
	1597,  1601,  1607,  1609,  1613,  1619,  1621,  1627,  1637,  1657,
	1663,  1667,  1669,  1693,  1697,  1699,  1709,  1721,  1723,  1733,
	1741,  1747,  1753,  1759,  1777,  1783,  1787,  1789,  1801,  1811,
	1823,  1831,  1847,  1861,  1867,  1871,  1873,  1877,  1879,  1889,
	1901,  1907,  1913,  1931,  1933,  1949,  1951,  1973,  1979,  1987,
	1993,  1997,  1999,  2003,  2011,  2017,  2027,  2029,  2039,  2053,
	2063,  2069,  2081,  2083,  2087,  2089,  2099,  2111,  2113,  2129,
	2131,  2137,  2141,  2143,  2153,  2161,  2179,  2203,  2207,  2213,
	2221,  2237,  2239,  2243,  2251,  2267,  2269,  2273,  2281,  2287,
	2293,  2297,  2309,  2311,  2333,  2339,  2341,  2347,  2351,  2357,
	2371,  2377,  2381,  2383,  2389,  2393,  2399,  2411,  2417,  2423,
	2437,  2441,  2447,  2459,  2467,  2473,  2477,  2503,  2521,  2531,
	2539,  2543,  2549,  2551,  2557,  2579,  2591,  2593,  2609,  2617,
	2621,  2633,  2647,  2657,  2659,  2663,  2671,  2677,  2683,  2687,
	2689,  2693,  2699,  2707,  2711,  2713,  2719,  2729,  2731,  2741,
	2749,  2753,  2767,  2777,  2789,  2791,  2797,  2801,  2803,  2819,
	2833,  2837,  2843,  2851,  2857,  2861,  2879,  2887,  2897,  2903,
	2909,  2917,  2927,  2939,  2953,  2957,  2963,  2969,  2971,  2999,
	3001,  3011,  3019,  3023,  3037,  3041,  3049,  3061,  3067,  3079,
	3083,  3089,  3109,  3119,  3121,  3137,  3163,  3167,  3169,  3181,
	3187,  3191,  3203,  3209,  3217,  3221,  3229,  3251,  3253,  3257,
	3259,  3271,  3299,  3301,  3307,  3313,  3319,  3323,  3329,  3331,
	3343,  3347,  3359,  3361,  3371,  3373,  3389,  3391,  3407,  3413,
	3433,  3449,  3457,  3461,  3463,  3467,  3469,  3491,  3499,  3511,
	3517,  3527,  3529,  3533,  3539,  3541,  3547,  3557,  3559,  3571};

GEMap::GEMap(){
	// Initialise parameters;
	grammarFile = GEMAP_DEFAULT_GRAMMAR;
	initialiser = GEMAP_DEFAULT_INITIALISER;
	maxWrap = GEMAP_DEFAULT_MAXWRAP;
	minRndGenomeSize = GEMAP_DEFAULT_MINRNDGENOMESIZE;
	maxRndGenomeSize = GEMAP_DEFAULT_MAXRNDGENOMESIZE;
	SIMinDepth = GEMAP_DEFAULT_SIMINDEPTH;
	SIMaxDepth = GEMAP_DEFAULT_SIMAXDEPTH;
	SITailRatio = GEMAP_DEFAULT_SITAILRATIO;
	// Initialise hash table;
	clearHash();
	blankFella.symbolName = "";
	blankFella.symbolType = GEMAP_UNDEF;
}

string GEMap::getParamsString(){
	return GEMAP_PARAMSSTRING;
}

string GEMap::getHelpString(){
	stringstream helpStr;
	helpStr
		<< "\t -G str \t file containing grammar (default "
		<< GEMAP_DEFAULT_GRAMMAR << ")\n"
		<< "\t -W int \t max wrapping events (default "
		<< GEMAP_DEFAULT_MAXWRAP << ")\n"
		<< "\t -N str \t initialiser (default "
		<< GEMAP_STRINITIALISER[GEMAP_DEFAULT_INITIALISER] << "); choices are\n";
	for(size_t ii = 0; ii < GEMAP_TOTINITIALISER; ++ii){
		helpStr << "\t\t\t " << GEMAP_STRINITIALISER[ii] << ":\t" << GEMAP_DETAILSINITIALISER[ii] << "\n";
	}
	helpStr
		<< "\t -z int \t minimum random init. genome size (default "
		<< GEMAP_DEFAULT_MINRNDGENOMESIZE << ")\n"
		<< "\t -Z int \t maximum random init. genome size (default "
		<< GEMAP_DEFAULT_MAXRNDGENOMESIZE << ")\n"
		<< "\t -d int \t sensible initialisation min depth (default "
		<< GEMAP_DEFAULT_SIMINDEPTH << " = use tree minimum depth)\n"
		<< "\t -D int \t sensible initialisation max depth (default "
		<< GEMAP_DEFAULT_SIMAXDEPTH << ")\n"
		<< "\t -T dbl \t sensible initialisation tail ratio (default "
		<< GEMAP_DEFAULT_SITAILRATIO << ")\n";
	return helpStr.str();
}

bool GEMap::paramClash(const string & otherParamsString){
	string myParamsString = GEMAP_PARAMSSTRING;
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
void GEMap::scanParams(int &argc, char **argv){
	return extractParams(argc, argv, false);
}

///////////////////////////////////////////////////////////////////////////////
// Receives argc and argv, sets argument values according to options,
// and extracts the relevant parameters if modifyArgv is true.
///////////////////////////////////////////////////////////////////////////////
void GEMap::extractParams(int &argc, char **argv, bool modifyArgv){
	opterr = 0;		// Don't quit if unknown params are found;
	optind = 1;		// Reset scanner;
	optopt = 0;		// Last unknown "valid" argument found;
	vector<char*> newargv;	// New argc and argv to return, with extracted options;
	char opt;
	while(optind < argc){
		opt = getopt(argc, argv, GEMAP_PARAMSSTRING);
		switch(opt){
			case 'G': setGrammarFile(string(optarg));
				break;
			case 'W': setMaxWraps(atoi(optarg));
				break;
			case 'N': setInitialiserStr(optarg);
				break;
			case 'z': setMinRndGenomeSize(atoi(optarg));
				break;
			case 'Z': setMaxRndGenomeSize(atoi(optarg));
				break;
			case 'd': setSIMinDepth(atoi(optarg));
				break;
			case 'D': setSIMaxDepth(atoi(optarg));
				break;
			case 'T': setSITailRatio(atof(optarg));
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
}

string GEMap::outputParams(const bool strip){
	stringstream output;
	output	<< (strip?"":" ") << "-G" << (strip?"":" ") << getGrammarFile()
		<< (strip?"":" ") << "-W" << (strip?"":" ") << getMaxWraps()
		<< (strip?"":" ") << "-N" << (strip?"":" ") << getInitialiserStr()
		<< (strip?"":" ") << "-z" << (strip?"":" ") << getMinRndGenomeSize()
		<< (strip?"":" ") << "-Z" << (strip?"":" ") << getMaxRndGenomeSize()
		<< (strip?"":" ") << "-d" << (strip?"":" ") << getSIMinDepth()
		<< (strip?"":" ") << "-D" << (strip?"":" ") << getSIMaxDepth()
		<< (strip?"":" ") << "-T" << (strip?"":" ") << getSITailRatio();
	return output.str();
}

string GEMap::getGrammarFile() const{
	return grammarFile;
}

void GEMap::setGrammarFile(const string &newGrammarFile){
	grammarFile = newGrammarFile;
}

size_t GEMap::getMaxWraps() const{
	return maxWrap;
}

void GEMap::setMaxWraps(const size_t &newMaxWrap){
	maxWrap = newMaxWrap;
}

size_t GEMap::getInitialiser() const{
	return initialiser;
}

void GEMap::setInitialiser(const size_t &newInitialiser){
	initialiser = newInitialiser;
}

string GEMap::getInitialiserStr() const{
	return GEMAP_STRINITIALISER[initialiser];
}

void GEMap::setInitialiserStr(const string &newStr){
	for(size_t ii = 0; ii < GEMAP_TOTINITIALISER; ++ii){
		if(!strcmp(newStr.c_str(), (GEMAP_STRINITIALISER[ii]).c_str())){
			initialiser = ii;
			return;
		}
	}
	cerr << "Unknown initialiser \"" << newStr << "\".\n";
	cerr << "Execution aborted.\n";
	exit(0);
}

size_t GEMap::getSIMinDepth() const{
	return SIMinDepth;
}

void GEMap::setSIMinDepth(const size_t &newSIMinDepth){
	SIMinDepth = newSIMinDepth;
	if(SIMinDepth > getSIMaxDepth()) setSIMaxDepth(SIMinDepth);
}

size_t GEMap::getSIMaxDepth() const{
	return SIMaxDepth;
}

void GEMap::setSIMaxDepth(const size_t &newSIMaxDepth){
	SIMaxDepth = newSIMaxDepth;
	if(SIMaxDepth < getSIMinDepth()) setSIMinDepth(SIMaxDepth);
}

size_t GEMap::getMinRndGenomeSize() const{
	return minRndGenomeSize;
}

void GEMap::setMinRndGenomeSize(const size_t &newMinRndGenomeSize){
	minRndGenomeSize = newMinRndGenomeSize;
}

size_t GEMap::getMaxRndGenomeSize() const{
	return maxRndGenomeSize;
}

void GEMap::setMaxRndGenomeSize(const size_t &newMaxRndGenomeSize){
	maxRndGenomeSize = newMaxRndGenomeSize;
}

double GEMap::getSITailRatio() const{
	return SITailRatio;
}

void GEMap::setSITailRatio(const double &newSITailRatio){
	if(newSITailRatio >= 0.0){
		SITailRatio = newSITailRatio;
	}
}

bool GEMap::readBNFFile(const string &filename, bool eBNF){
	string grammar, gLine;
	ifstream gFile(filename.c_str());
	if(!gFile.is_open()){
		cerr << "Could not open grammar file '" << filename << "'.\n";
		return false;
	}
	while(!gFile.eof()){
		getline(gFile, gLine);
		grammar += gLine + "\n";
	}
	gFile.close();
	return readBNFString(grammar, eBNF);
}

bool GEMap::readBNFString(const string &grammar, bool eBNF){
	if(!tokenise(grammar, eBNF)){
		return false;
	}
	if(DEBUGGING){
		for(size_t hi = 0; hi < hTable.size(); ++hi){
			if(!hTable[hi].size()){
				cout << "-";
				continue;
			}
			cout << "\n" << hi << " (size = " << hTable[hi].size() << ")\n";
			for(size_t ho = 0; ho < hTable[hi].size(); ++ho){
				cout << "'" << hTable[hi][ho].symbolName << "' (" <<
					hTable[hi][ho].symbolType << ") " <<
					(hTable[hi][ho].recRule ? "" : "non-") <<
					"recursive, minDepth " << hTable[hi][ho].minDepth <<
					" [" << hTable[hi][ho].minRepeat << ":" << hTable[hi][ho].maxRepeat << "] :\n";
				for(size_t kk = 0; kk < hTable[hi][ho].prods.size(); ++kk){
					cout << "   ";
					for(size_t ll = 0; ll < hTable[hi][ho].prods[kk].size(); ++ll){
						cout << " '" << hTable[hi][ho].prods[kk][ll] << "'";
					}
					cout << " " << (hTable[hi][ho].recProds[kk] ? "" : "non-") <<
						"recursive, depth " << hTable[hi][ho].depthProds[kk] <<
						".\n";
				}
			}
		}
		cout << "\n";
		for(size_t ii = 0; ii < tokens.size(); ++ii)
			if(DEBUGGING) cout << " '" << tokens[ii] << "'";
		cout << "\n";
	}
	size_t ti = 0; // Token index;
	size_t state = GEMAP_RULEDEF; // Parser state;
	string currentRule = ""; // NT whose productions are being defined;
	vector<string> currentProduction; // Current production being defined;
	startSymbol = tokens[0]; // By default: can be changed without reparsing;
	while(ti < tokens.size()){
		geMapHash token = hDef(tokens[ti]);
		if(DEBUGGING) cout << "-> Dealing with token '" << tokens[ti] << "':\n";
		switch(state){
			case(GEMAP_RULEDEF):
				// Ignore spaces;
				if(token.symbolType == GEMAP_SP){
					if(DEBUGGING) cout << "\tIgnoring space.\n";
					break;
				}
				// Otherwise this is a non-terminal symbol,
				// unless the grammar is really crappy;
				if(token.symbolType == GEMAP_NT || token.symbolType == GEMAP_T){
					// Add previously defined production;
					// Remove all spaces preceding the NT;
					while(currentProduction.size() && currentProduction.back() == " "){
						currentProduction.pop_back();
						if(DEBUGGING) cout << "\tPopping out space.\n";
					}
					if(currentRule.size() != 0){
						// Add production to symbol, which is already
						// in hash table (tokenise() did that); correct
						// symbol type so that it is NT;
						hashAddProduction(currentRule, GEMAP_NT, currentProduction);
					}
					currentRule = tokens[ti];
					currentProduction.clear();
					if(DEBUGGING) cout << "\tDefining rule for token '" << tokens[ti] << "'.\n";
					state = GEMAP_SIGNDEF;
					break;
				}
				// Still here? Syntax error on grammar;
				cerr << "Syntax error reading grammar.\n";
				return false;
			case(GEMAP_SIGNDEF):
				// Ignore spaces;
				if(token.symbolType == GEMAP_SP){
					if(DEBUGGING) cout << "\tIgnoring space.\n";
					break;
				}
				if(token.symbolType == GEMAP_DEF){
					if(DEBUGGING) cout << "\tGot defsign ok.\n";
					state = GEMAP_PRULE;
					// Ignore all spaces following SIGNDEF;
					while(ti < tokens.size() - 1 &&
						hDef(tokens[ti + 1]).symbolType == GEMAP_SP){
						ti++;
						if(DEBUGGING) cout << "\tSkipping space.\n";
					}
					break;
				}
				// Still here? Syntax error on grammar;
				cerr << "Syntax error reading grammar.\n";
				return false;
			case(GEMAP_PRULE):
				if(token.symbolType == GEMAP_SP){
					// Separation space; just add one;
					if(ti && hDef(tokens[ti - 1]).symbolType != GEMAP_SP){
						currentProduction.push_back(tokens[ti]);
						if(DEBUGGING) cout << "\tAdded space to current production.\n";
					}
					break;
				}
				if(token.symbolType == GEMAP_OR){
					// Terminate production, start new one;
					if(DEBUGGING) cout << "\tFinishing production, inserting onto '" << currentRule << "' rule.\n";
					// Remove all spaces preceding OR;
					while(currentProduction.size() && currentProduction.back() == " "){
						currentProduction.pop_back();
						if(DEBUGGING) cout << "\tPopping out space.\n";
					}
					// Add production to currentRule (which is a NT);
					hashAddProduction(currentRule, GEMAP_NT, currentProduction);
					currentProduction.clear();
					// Ignore all spaces following OR;
					while(ti < tokens.size() - 1 &&
						hDef(tokens[ti + 1]).symbolType == GEMAP_SP){
						ti++;
						if(DEBUGGING) cout << "\tSkipping space.\n";
					}
					break;
				}
				if(token.symbolType == GEMAP_T || token.symbolType == GEMAP_NT
					|| token.symbolType == GEMAP_XO){
					if(DEBUGGING && token.symbolType == GEMAP_XO){
						cout << "\tXO marker.\n";
					}
					// Look ahead: skip all spaces until a new token is found;
					size_t lookahead = ti + 1;
					while(lookahead < tokens.size() &&
						hDef(tokens[lookahead]).symbolType == GEMAP_SP){
						lookahead++;
						if(DEBUGGING) cout << "\tLooking ahead, skipping space.\n";
					}
					// If next token (apart from spaces) is DEF,
					// then this is a new rule def;
					if(lookahead < tokens.size() &&
						hDef(tokens[lookahead]).symbolType == GEMAP_DEF){
						// Found a ::=, so put back the token,
						// and change to GEMAP_RULEDEF;
						ti--;// Put back token;
						if(DEBUGGING) cout << "\tPut back symbol, changed to GEMAP_RULEDEF.\n";
						state = GEMAP_RULEDEF;
						break;
					}
					// Otherwise add symbol to current production;
					currentProduction.push_back(tokens[ti]);
					if(DEBUGGING && lookahead < tokens.size()) cout << "\tNext ('" << tokens[lookahead] << "') not SIGNDEF, added symbol to current production.\n";
					break;
				}
				if(token.symbolType == GEMAP_QT && ti + 1 < tokens.size()){
					// If next token is GEMAP_T or GEMAP_NT or GEMAP_SP,
					// then quantifier is valid and correctly placed;
					if(DEBUGGING) cout << "\tQuantifier found.\n";
					geMapHash &nextToken = hDef(tokens[ti + 1]);
					if(nextToken.symbolType == GEMAP_T
						|| nextToken.symbolType == GEMAP_NT
						|| nextToken.symbolType == GEMAP_SP){
						if(DEBUGGING) cout << "\tLooked ahead, correctly placed, so added to production.\n";
						// Otherwise add symbol to current production;
						currentProduction.push_back(tokens[ti]);
						break;
					}
					// Otherwise quantifier is wrongly placed;
					cerr << "Syntax error reading grammar (incorrect quantifier).\n";
					return false;
				}
				// Still here? Syntax error on grammar;
				cerr << "Syntax error reading grammar.\n";
				return false;
			default: cerr << "Syntax rule reading grammar.\n";
				return false;
		}
		ti++;
	}
	// At end of grammar, add last production;
	if(currentRule.size()){
		if(DEBUGGING) cout << "Adding last production.\n";
		// Remove all trailing spaces;
		while(currentProduction.size() && currentProduction.back() == " "){
			currentProduction.pop_back();
			if(DEBUGGING) cout << "\tPopping out space.\n";
		}
		if(currentRule.size()){
			hashAddProduction(currentRule, GEMAP_NT, currentProduction);
		}
	}
	// Update all recursion and minimum depth flags;
	updateRecAndDepth();
	if(DEBUGGING) for(size_t hi = 0; hi < hTable.size(); ++hi){
		if(!hTable[hi].size()){
			cout << "-" << hi;
			continue;
		}
		cout << "\n" << hi << " (size = " << hTable[hi].size() << ")\n";
		for(size_t ho = 0; ho < hTable[hi].size(); ++ho){
			cout << "'" << hTable[hi][ho].symbolName << "' (" <<
				hTable[hi][ho].symbolType << ") " <<
				(hTable[hi][ho].recRule ? "" : "non-") <<
				"recursive, minDepth " << hTable[hi][ho].minDepth <<
				" [" << hTable[hi][ho].minRepeat << ":" << hTable[hi][ho].maxRepeat << "] :\n";
			for(size_t kk = 0; kk < hTable[hi][ho].prods.size(); ++kk){
				cout << "   ";
				for(size_t ll = 0; ll < hTable[hi][ho].prods[kk].size(); ++ll){
					cout << " '" << hTable[hi][ho].prods[kk][ll] << "'";
				}
				cout << " " << (hTable[hi][ho].recProds[kk] ? "" : "non-") <<
					"recursive, depth " << hTable[hi][ho].depthProds[kk] <<
					".\n";
			}
		}
	}
	if(DEBUGGING) cout << "\n";
	return true;
}

bool GEMap::readEBNFFile(const string &filename){
	return readBNFFile(filename, true);
}

bool GEMap::readEBNFString(const string &grammar){
	return readBNFString(grammar, true);
}

///////////////////////////////////////////////////////////////////////////////
// mapGE
// Wrapper for calling the main mapGE method, without keeping productions,
// XO sites and extra flags.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::mapGE(const vector<size_t> &codons, string &phenotype,
	size_t &effSize, size_t maxIterations){
	// Dummy args for mapGE;
	vector<size_t> dummy1;
	vector<size_t> dummy2;
	vector<string> dummy3;
	map<string, double> dummy4;
	return mapGE(codons, phenotype, effSize, dummy1, dummy2, dummy3,
		dummy4, maxIterations, false);
}

///////////////////////////////////////////////////////////////////////////////
// mapGE
// Wrapper for calling the main mapGE method, without keeping productions
// and extra flags
///////////////////////////////////////////////////////////////////////////////
bool GEMap::mapGE(const vector<size_t> &codons, string &phenotype,
	size_t &effSize, vector<size_t> &xoSites, size_t maxIterations){
	// Dummy args for mapGE;
	vector<size_t> dummy1;
	vector<string> dummy2;
	map<string, double> dummy3;
	return mapGE(codons, phenotype, effSize, xoSites, dummy1, dummy2,
		dummy3, maxIterations, false);
}

///////////////////////////////////////////////////////////////////////////////
// mapGE
// Wrapper for calling the main mapGE method, without keeping XO sites
// and extra flags
///////////////////////////////////////////////////////////////////////////////
bool GEMap::mapGE(const vector<size_t> &codons, string &phenotype,
	size_t &effSize, vector<size_t> &productions,
	vector<string> &nonTerminals, size_t maxIterations,
	bool pushProductions){
	// Dummy args for mapGE;
	vector<size_t> dummy1;
	map<string, double> dummy2;
	return mapGE(codons, phenotype, effSize, dummy1, productions, nonTerminals,
		dummy2, maxIterations, pushProductions);
}

///////////////////////////////////////////////////////////////////////////////
// mapGE
// Wrapper for calling the main mapGE method, without keeping productions or
// XO sites.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::mapGE(const vector<size_t> &codons, string &phenotype,
	size_t &effSize, map<string, double> &extra, size_t maxIterations){
	// Dummy args for mapGE;
	vector<size_t> dummy1;
	vector<size_t> dummy2;
	vector<string> dummy3;
	return mapGE(codons, phenotype, effSize, dummy1, dummy2, dummy3,
		extra, maxIterations, false);
}

///////////////////////////////////////////////////////////////////////////////
// mapGE
// Wrapper for calling the main mapGE method, without keeping productions.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::mapGE(const vector<size_t> &codons, string &phenotype,
	size_t &effSize, vector<size_t> &xoSites, map<string, double> &extra,
	size_t maxIterations){
	// Dummy args for mapGE;
	vector<size_t> dummy1;
	vector<string> dummy2;
	return mapGE(codons, phenotype, effSize, xoSites, dummy1, dummy2,
		extra, maxIterations, false);
}

///////////////////////////////////////////////////////////////////////////////
// mapGE
// Wrapper for calling the main mapGE method, without keeping XO sites.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::mapGE(const vector<size_t> &codons, string &phenotype,
	size_t &effSize, vector<size_t> &productions,
	vector<string> &nonTerminals, map<string, double> &extra,
	size_t maxIterations, bool pushProductions){
	// Dummy args for mapGE;
	vector<size_t> dummy1;
	return mapGE(codons, phenotype, effSize, dummy1, productions, nonTerminals,
		extra, maxIterations, pushProductions);
}

///////////////////////////////////////////////////////////////////////////////
// mapGE
// Standard GE mapping. The codons are used to choose productions
// from the grammar. The chosen productions are pushed onto a vector, and the
// NT symbols they were chosen from as well, if pushProductions is set.
// If eBNF, XOSites found are are also pushed onto a vector.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::mapGE(const vector<size_t> &codons, string &phenotype,
	size_t &effSize, vector<size_t> &xoSites, vector<size_t> &productions,
	vector<string> &nonTerminals, map<string, double> &extra,
	size_t maxIterations, bool pushProductions){
	if(hTable.size() <= 2){
		// Empty hash table;
	}
	phenotype = "";
	effSize = 0; // codons index;
	xoSites.clear();
	productions.clear();
	nonTerminals.clear();
	stack<string> symbols; // Symbol stack;
	symbols.push(startSymbol);
	size_t wraps = 0;
	if(!codons.size())
		return false;
	while(((getMaxWraps() && wraps <= getMaxWraps()) || effSize < codons.size())
		&& symbols.size() && maxIterations){
		maxIterations--;
		size_t cIndex = effSize % codons.size();
		if(DEBUGGING) cout << "===== Dealing with codon " << codons[cIndex] << ", at index " <<
			effSize << "(" << cIndex << "), top of symbols is " <<
			(symbols.size()?symbols.top():"empty") << ".\n";
		// Locate definition of next NT to map;
		string mapSymb = symbols.top();
		symbols.pop();
		geMapHash &def = hDef(mapSymb);
		switch(def.symbolType){
			case GEMAP_XO:
				xoSites.push_back(cIndex);
				break;
			case GEMAP_T:
			case GEMAP_SP:
				// Terminal symbol, add straight to phenotype;
				phenotype += mapSymb;
				if(DEBUGGING) cout << "Symbol '" << mapSymb <<
					"' added straight to phenotype.\n";
				break;
			case GEMAP_UNDEF:
				// NT not defined, treated as terminal;
				cerr << "Symbol '" << mapSymb << "' not found in hash table.\n";
				break;
			case GEMAP_QT:{
				// Choose a number of repeats;
				size_t symbolRepeats = def.minRepeat; // Default value;
				if(DEBUGGING) cout << "Quantifier '" << mapSymb << "' found.\n";
				if(def.maxRepeat != def.minRepeat){
					// Range of repeat values, consume codon to choose;
					symbolRepeats = codons[cIndex] %
						(def.maxRepeat - def.minRepeat + 1) + def.minRepeat;
					if(DEBUGGING) cout << "Chose to repeat " << symbolRepeats
						<< " times = " << codons[cIndex] << " % (" 
						<< def.maxRepeat << " - " << def.minRepeat <<
						" + 1) + " << def.minRepeat << ".\n";
					//if(++effSize >= codons.size() && getMaxWraps()){
					if(((++effSize % codons.size()) == 0) && getMaxWraps()){
						wraps++;
						cIndex = effSize % codons.size();
						if(DEBUGGING) cout << wraps << " wrap events.\n";
					}
				}
				// Pop and keep next (quantified) symbol;
				if(!symbols.size()){
					cerr << "Bad quantifier (maybe unescaped '[]'"
						<< " characters in grammar?).\n"
						<< "Execution aborted.\n";
					exit(0);
				}
				string repeatSymb = symbols.top();
				symbols.pop();
				// Now push symbolRepeats copies of it back onto stack;
				for(size_t ii = 0; ii < symbolRepeats; ++ii){
					symbols.push(repeatSymb);
				}
				if(DEBUGGING) cout << "Pushed " << symbolRepeats << " copies of '" <<
					repeatSymb <<"' onto the stack.\n";
				break;
				}
			case GEMAP_NT:{
				// Choose an available production;
				size_t choice = 0;
				if(def.prods.size() > 1){
					// More than one production, consume codon to choose;
					choice = codons[cIndex] % def.prods.size();
					if(DEBUGGING) cout << "Chose production " << choice << " = " <<
						codons[cIndex] << " % " <<
						def.prods.size() << ": replacing '" << mapSymb << "' with";
					//if(++effSize >= codons.size() && getMaxWraps()){
					if(((++effSize % codons.size()) == 0) && getMaxWraps()){
						wraps++;
						cIndex = effSize % codons.size();
						if(DEBUGGING) cout << wraps << " wrap events.\n";
					}
				}
				for(size_t ii = 0; ii < def.prods[choice].size(); ++ii){
					if(DEBUGGING) cout << " '" << def.prods[choice][ii] << "'";
				}
				if(DEBUGGING) cout << ". Codon index is " << effSize << "(" << cIndex
					<< ") / " << codons.size() << ".\n";
				// Insert starting terminal symbols of production onto phenotype
				size_t prodIndex = 0;
				while(prodIndex < def.prods[choice].size() &&
					(hDef(def.prods[choice][prodIndex]).symbolType == GEMAP_T ||
					hDef(def.prods[choice][prodIndex]).symbolType == GEMAP_SP)){
					if(DEBUGGING) cout << "\tAdding '" <<
						def.prods[choice][prodIndex] << "' to phenotype.\n";
					phenotype += def.prods[choice][prodIndex];
					prodIndex++;
				}
				// Now insert remaining symbols onto stack, reverse order;
				if(DEBUGGING) cout << "prodIndex == '" << prodIndex << "'.\n";
				for(size_t ii = def.prods[choice].size(); ii > prodIndex; --ii){
					symbols.push(def.prods[choice][ii - 1]);
					if(DEBUGGING) cout << "Pushed '" << def.prods[choice][ii - 1]
						<< "' onto stack.\n";
				}
				if(DEBUGGING) cout << "Phenotype == '" << phenotype << "'.\n";
				if(DEBUGGING) if(symbols.size()) cout << "Top of symbols == '" <<
					symbols.top() << "'.\n";
				if(pushProductions){
					nonTerminals.push_back(def.symbolName);
					productions.push_back(choice);
				}
				break;
				}
			default:
				cerr << "Undeclared symbol type? in mapGE()? Execution aborted.\n";
				exit(0);
		}
		// Empty terminal symbols on top of stack straight onto phenotype;
		while(symbols.size()){
			geMapHash &topOfStack = hDef(symbols.top());
			if(topOfStack.symbolType == GEMAP_NT || topOfStack.symbolType == GEMAP_QT
				|| topOfStack.symbolType == GEMAP_XO){
				break;
			}
			// Don't ignore GEMAP_XO, or it won't be added to xomarkers list
			//if(topOfStack.symbolType != GEMAP_XO){
				if(DEBUGGING) cout << "Adding terminal '"
					<< topOfStack.symbolName << "' straight to phenotype.\n";
				phenotype += topOfStack.symbolName;
			//}
			//else{
			//	if(DEBUGGING) cout << "Ignoring '" << topOfStack.symbolName
			//		<< "'.\n";
			//}
			symbols.pop();
		}
	}
	// Handle pecial wrap case;
	if((effSize % codons.size()) == 0 && wraps){
		wraps--;
	}
	if(wraps){
		extra["wraps"] = wraps;
	}
	else{
		extra.erase("wraps");
	}
	//if(effSize > codons.size()){
	//	effSize = codons.size();
	//}
	if(DEBUGGING){
		if(getMaxWraps() && wraps >= getMaxWraps())
			cout << "Maximum number of wrapping events reached in mapGE; "
				<< symbols.size() << " unmapped symbols remaining.\n";
	}
	if(!maxIterations){
		cerr << "Maximum number of mapping iterations reached in mapGE.\n";
	}
	if(symbols.size()){
		do{
			phenotype += symbols.top();
			symbols.pop();
		} while(symbols.size());
		return false;
	}
	return true;
}

///////////////////////////////////////////////////////////////////////////////
// unmapGE
// Reverse GE mapping. Given an input phenotype string, this method attempts
// to reverse map it onto a string of grammar choices, using an LALR(1)
// parser, based on the current grammar.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::unmapGE(const string &phenotype, vector<size_t> &codons){
	// First detect all terminal symbols, to identify in lexer;
	return false;
}

///////////////////////////////////////////////////////////////////////////////
// initGE
// Launcher with no crossover locations recorded and no index specified.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::initGE(vector<size_t> &codons, string &phenotype, size_t &effSize,
	const bool &grow, const size_t &maxVal){
	vector<size_t> xoMarkers;
	double index = 1.0;
	return initGE(codons, phenotype, effSize, xoMarkers, index, grow, maxVal);
}

///////////////////////////////////////////////////////////////////////////////
// initGE
// Launcher with no crossover locations recorded.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::initGE(vector<size_t> &codons, string &phenotype, size_t &effSize,
	const double &index, const bool &grow, const size_t &maxVal){
	vector<size_t> xoMarkers;
	return initGE(codons, phenotype, effSize, xoMarkers, index, grow, maxVal);
}

///////////////////////////////////////////////////////////////////////////////
// initGE
// Launcher with no index specified.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::initGE(vector<size_t> &codons, string &phenotype, size_t &effSize,
	vector<size_t> &xoMarkers, const bool &grow, const size_t &maxVal){
	double index = 1.0;
	return initGE(codons, phenotype, effSize, xoMarkers, index, grow, maxVal);
}

///////////////////////////////////////////////////////////////////////////////
// initGE
// Call selected initialiser method.
// Index argument specifies percentile index of individual in population.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::initGE(vector<size_t> &codons, string &phenotype, size_t &effSize,
	vector<size_t> &xoMarkers, const double &index, const bool &grow,
	const size_t &maxVal){
	if(index < 0.0 || index > 1.0){
		cerr << "GEMap initialiser index must be between 0.0 and 1.0.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	switch(getInitialiser()){
		case GEMAP_RND:
				return rInitGE(codons, phenotype, effSize, xoMarkers,
					index, grow, maxVal);
		case GEMAP_SI:
				return sInitGE(codons, phenotype, effSize, xoMarkers,
					index, grow, maxVal);
		default:	cerr << "Unknown GE initialiser.\nExecution aborted.";
				exit(0);
	}
}

///////////////////////////////////////////////////////////////////////////////
// rInitGE
// Random Initialisation for GE. The input codons vector will contain the
// initialised genotype after the function call, will have a size between
// minRndGenomeSize and maxRndGenomeSize, and codons will have values between 0
// and maxVal. The generated phenotype is also returned.0
///////////////////////////////////////////////////////////////////////////////
bool GEMap::rInitGE(vector<size_t> &codons, string &phenotype, size_t &effSize,
	vector<size_t> &xoMarkers, const double &index, const bool &grow,
	const size_t &maxVal){
	if(index < 0.0 || index > 1.0){
		cerr << "Index on GE initialisation must be between 0.0 and 1.0 .\n"
			<< "Execution aborted.\n";
		assert(index >= 0.0 && index <= 1.0);
	}
	if(getMinRndGenomeSize() > getMaxRndGenomeSize()){
		cerr << "Minimum random init. genome size must be <= to maximum.\n"
			<< "Execution aborted.\n";
		assert(getMinRndGenomeSize() <= getMaxRndGenomeSize());
	}
	// Prepare genotype;
	codons.clear();
	size_t length;
	if(grow) length = rand() % (getMaxRndGenomeSize() - getMinRndGenomeSize() + 1)
		+ getMinRndGenomeSize();
	else length = index * (getMaxRndGenomeSize() - getMinRndGenomeSize())
		+ getMinRndGenomeSize();
	for(size_t ii = 0; ii < length; ++ii) codons.push_back(rand() % maxVal);
	bool valid = mapGE(codons, phenotype, effSize, xoMarkers);
	if(DEBUGGING) cout << "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n";
	if(DEBUGGING) cout << "+ LENGTH = " << length << " (between " << getMinRndGenomeSize()
		<< " and " << getMaxRndGenomeSize() << "); " << (valid ? "" : "not ") << "valid.\n";
	return valid;
}

///////////////////////////////////////////////////////////////////////////////
// sInitGE
// Sensible Initialisation for GE. The input codons vector will contain the
// initialised genotype after the function call. Derivation trees will be
// grown to the specified depth, if grow is not set (or if set to false);
// otherwise trees will have a depth up to (but not necessarily equal to)
// the specified depth. The generated phenotype is also returned.
// Codon values will be unmodded to the value maxVal.
// Main job is done in recursive function sInitGEExpand().
///////////////////////////////////////////////////////////////////////////////
bool GEMap::sInitGE(vector<size_t> &codons, string &phenotype, size_t &effSize,
	vector<size_t> &xoMarkers, const double &index, const bool &grow,
	const size_t &maxVal){
	if(getSIMinDepth() && hDef(startSymbol).minDepth > static_cast<int>(getSIMinDepth())){
		cerr << "Grammar requires minimum SI depth of at least "
			<< hDef(startSymbol).minDepth << ".\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	if(static_cast<int>(getSIMaxDepth()) < hDef(startSymbol).minDepth){
		cerr << "Depth " << getSIMaxDepth() << " smaller than minimum depth which is "
			<< hDef(startSymbol).minDepth << ".\n"
			<< "Execution aborted at sInitGE().\n";
		exit(0);
		return "";
	}
	// Calculate which depth to go to;
	int sSMinDepth = max(hDef(startSymbol).minDepth, static_cast<int>(getSIMinDepth()));
	size_t increment = min(static_cast<size_t>(index * (getSIMaxDepth() - sSMinDepth + 1)),
		getSIMaxDepth() - sSMinDepth);
	size_t finalDepth = sSMinDepth + increment;
	if(DEBUGGING)
		cout << "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n"
		<< "+ DEPTH = " << sSMinDepth << " + " << increment << " for index "
		<< index << "; '" << startSymbol << "' minimum depth = "
		<< hDef(startSymbol).minDepth << "; we are "
		<< (grow? "growing.\n" : "fulling.\n");
	codons.clear();
	phenotype = "";
	size_t lowestDepth = sInitGEExpand(startSymbol, codons, xoMarkers,
		finalDepth, maxVal, grow, phenotype);
	if(DEBUGGING) cout << "+ Phenotype = '" << phenotype << "'.\n";
	size_t maxTries = 1000;
	while(finalDepth - lowestDepth + 1 < getSIMinDepth() && maxTries){
		if(DEBUGGING)
			cout << "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n"
			<< "+ NEW TRY, ONLY WENT TO DEPTH " << finalDepth - lowestDepth + 1 << "\n"
			<< "+ DEPTH = " << sSMinDepth << " + " << increment << " for index "
			<< index << "; '" << startSymbol << "' minimum depth = "
			<< hDef(startSymbol).minDepth << "; we are "
			<< (grow? "growing.\n" : "fulling.\n");
		codons.clear();
		xoMarkers.clear();
		phenotype = "";
		lowestDepth = sInitGEExpand(startSymbol, codons, xoMarkers,
			sSMinDepth + increment, maxVal, grow, phenotype);
		if(DEBUGGING) cout << "+ Phenotype = '" << phenotype << "'.\n";
		maxTries--;
	}
	if(!maxTries){
		cerr << "Could not use sensible initialisation to create individual of depth "
			<< sSMinDepth + increment << " with " << (grow?"grow":"full") << " method.\n"
			<< "Execution aborted.\n";
		exit(0);
	}
	size_t extraCodons = static_cast<size_t>(codons.size() * getSITailRatio());
	effSize = codons.size();
	for(size_t ii = 0; ii < extraCodons; ++ii){
		codons.push_back(rand() % maxVal);
	}
	return true;
}

///////////////////////////////////////////////////////////////////////////////////
// sInitGEExpand
// Helper function for sInitGE().
///////////////////////////////////////////////////////////////////////////////
size_t GEMap::sInitGEExpand(string &toMap, vector<size_t> &codons,
	vector<size_t> &xoMarkers, const size_t depth,
	const size_t &maxVal, const bool &grow, string &phenotype){
	if(DEBUGGING) cout << "++ " << (grow?"Growing":"Fullying") <<
		" symbol '" << toMap << "' to depth " << depth << ":\n";
	// Locate symbol;
	geMapHash &symb = hDef(toMap);
	// If symbol is T, leave;
	if(symb.symbolType == GEMAP_T || symb.symbolType == GEMAP_SP){
		phenotype += toMap;
		return depth;
	}
	// If symbol is XO, insert marker and then leave;
	if(symb.symbolType == GEMAP_XO){
		xoMarkers.push_back(codons.size());
		return depth;
	}
	// Vector of possible productions (indexes of symb.prods);
	vector<size_t> possibleProds;
	bool someAreRecursive = false;
	if(DEBUGGING) cout << "++++ Possible productions:";
	for(size_t pi = 0; pi < symb.prods.size(); ++pi){
		if(DEBUGGING) cout << " " << pi << "(" << symb.depthProds[pi] << "," <<
			(symb.recProds[pi] ? "r" : "n") << ")";
		if(static_cast<size_t>(symb.depthProds[pi]) < depth){
			possibleProds.push_back(pi);
			someAreRecursive = someAreRecursive || symb.recProds[pi];
			if(DEBUGGING) cout << "[OK]";
		}
		else{
			if(DEBUGGING) cout << "[KO]";
		}
		if(someAreRecursive){
			if(DEBUGGING) cout << "[R]";
		}
	}
	if(DEBUGGING) cout << "\n";
	// Randomly choose one of the elected productions;
	// If fullying, preferentially choose recursive rules;
	size_t chosenProd = rand() % possibleProds.size();
	while(!grow && someAreRecursive && !symb.recProds[possibleProds[chosenProd]]){
		chosenProd = rand() % possibleProds.size();
	}
	size_t chosenProdIndex = possibleProds[chosenProd];
	if(DEBUGGING) cout << "++++ Chose production " << chosenProdIndex << ": '" << toMap << "' ->";
	if(DEBUGGING) for(size_t ii = 0; ii < symb.prods[chosenProdIndex].size(); ++ii) cout << " '" << symb.prods[chosenProdIndex][ii] << "'";
	if(DEBUGGING) cout << ".\n";
	// If more than one production, unmod choice and push back onto codons;
	if(symb.prods.size() > 1){
		if(DEBUGGING) cout << "++++ Unmodding by " << chosenProdIndex << " + " <<
			symb.prods.size() << " * [0..(" << maxVal << "-" <<
			chosenProdIndex << ")/" << symb.prods.size() << "].\n";
		codons.push_back(chosenProdIndex + symb.prods.size()
			//* (rand() % ((maxVal - chosenProdIndex) / symb.prods.size())));
			* ((maxVal - chosenProdIndex) / symb.prods.size() == 0 ? 0: (rand() % ((maxVal - chosenProdIndex) / symb.prods.size()))));
		if(DEBUGGING) cout << "++++ Pushed codon " << codons.back() << " % " << symb.prods.size() << " = " << codons.back() % symb.prods.size() << ".\n";
	}
	// Now recursively descend onto each symbol on production;
	size_t si = 0;
	size_t lowestDepth = depth;
	while(si < symb.prods[chosenProdIndex].size()){
		geMapHash &prodSymb = hDef(symb.prods[chosenProdIndex][si]);
		size_t nbrReps = 1; // Default without quantifier;
		// If symbol is a quantifier, go down on the symbol following it 
		if(prodSymb.symbolType == GEMAP_QT){
			// Choose number of repetitions;
			if(prodSymb.maxRepeat > prodSymb.minRepeat){
				nbrReps = rand() % (prodSymb.maxRepeat - prodSymb.minRepeat + 1)
					+ prodSymb.minRepeat;
			}
			else{
				nbrReps = prodSymb.maxRepeat;
			}
			if(DEBUGGING) cout << "++++ Repeat '" << symb.prods[chosenProdIndex][si + 1] <<
				"' between " << prodSymb.minRepeat << " and " << prodSymb.maxRepeat << " times.\n";
			if(DEBUGGING) cout << "++++ Chose to repeat '" << symb.prods[chosenProdIndex][si + 1] <<
				"' " << nbrReps << " times.\n";
			if(prodSymb.maxRepeat > prodSymb.minRepeat){
				if(DEBUGGING) cout << "++++ Unmodding by " << nbrReps << " + " <<
					(prodSymb.maxRepeat - prodSymb.minRepeat + 1) << " * [0..(" << maxVal << "-" <<
					nbrReps << ")/" << (prodSymb.maxRepeat - prodSymb.minRepeat + 1) << "].\n";
				codons.push_back(nbrReps + (prodSymb.maxRepeat - prodSymb.minRepeat + 1)
					//* (rand() % ((maxVal - nbrReps) / (prodSymb.maxRepeat - prodSymb.minRepeat + 1))));
					* (rand() % (maxVal - nbrReps == 0 ? 0 : (maxVal - nbrReps) / (prodSymb.maxRepeat - prodSymb.minRepeat + 1))));
				if(DEBUGGING) cout << "++++ Pushed codon " << codons.back() << " % " << (prodSymb.maxRepeat - prodSymb.minRepeat + 1) << " = " << codons.back() % (prodSymb.maxRepeat - prodSymb.minRepeat + 1) << ".\n";
			}
			// Now adjust symbol index, to grow the following symbol nbrReps times;
			si++;
		}
		if(DEBUGGING) cout << "Going down on '" << symb.prods[chosenProdIndex][si] << "' " << nbrReps << " times.\n";
		for(size_t ii = 0; ii < nbrReps; ++ii){
			size_t newDepth = sInitGEExpand(symb.prods[chosenProdIndex][si], codons,
				xoMarkers, depth - 1, maxVal, grow, phenotype);
			if(newDepth < lowestDepth) lowestDepth = newDepth;
		}
		si++;
	}
	return lowestDepth;
}

///////////////////////////////////////////////////////////////////////////////
// Go through each character of the grammar, detect tokens, insert them
// into the hash table. Types will be amended by parser.
// eBNF flag adds detection of quantifiers.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::tokenise(const string &grammar, const bool &eBNF){
	stringstream wordCounter(grammar);
	string word;
	size_t totalWords = 0;
	while(wordCounter >> word){
		totalWords++;
	}
	clearHash(totalWords);
	tokens.clear();
	string token = "";
	bool escaped = false;
	bool quoted = false;
	bool doublequoted = false;
	bool squarebracketed = false;
	bool containsEscaped = false;
	bool terminal = false;
	vector<int> repeatValues; // Contains repeat values from quantifiers;
	size_t gi = 0; // Grammar index;
	while(gi < grammar.size()){
		// Special section for escaped characters;
		if(escaped){
			escaped = false;
			switch(grammar[gi]){
				case 'n':
					token += '\n';
					break;
				case 'r':
					token += '\r';
					break;
				case 'f':
					token += '\f';
					break;
				case 't':
					token += '\t';
					break;
				case 'a':
					token += '\a';
					break;
				case 'b':
					token += '\b';
					break;
				case 'v':
					token += '\v';
					break;
				case 'x':{
					// Possible hexadecimal number;
					// Check ahead to see if it is so;
					string sNumber;
					size_t tgi = gi + 1;
					while(tgi < grammar.size()){
						// Char is part of hexadecimal sequence;
						if((grammar[tgi] >= '0' && grammar[tgi] <= '9') ||
						(grammar[tgi] >= 'a' && grammar[tgi] <= 'f') ||
						(grammar[tgi] >= 'A' && grammar[tgi] <= 'F')){
							sNumber += grammar[tgi];
						}
						// Else leave;
						else{
							break;
						}
						tgi++;
					}
					// Should be an hexadecimal number;
					if(sNumber.size()){
						// Convert to hexadecimal;
						stringstream hexConv;
						hexConv << hex << sNumber;
						size_t hexaNumber;
						hexConv >> hexaNumber;
						if(hexaNumber > 255){
							cerr << "Warning: hex escape sequence\
								out of range.\n";
						}
						else{
							token += static_cast<char>(hexaNumber);
						}
						// Adjust index;
						gi = tgi - 1;
						break;
					}
					// Was not an hexadecimal number, add x;
					token += grammar[gi];
					break;
					}
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':{
					// Octal number;
					// Check ahead to build it;
					string sNumber;
					sNumber += grammar[gi];
					size_t tgi = gi + 1;
					while(tgi < grammar.size()){
						// Char is part of octal sequence;
						if(grammar[tgi] >= '0' && grammar[tgi] <= '7'){
							sNumber += grammar[tgi];
						}
						// Else leave;
						else{
							break;
						}
						// If number is complete;
						if(sNumber.size() == 3){
							break;
						}
						tgi++;
					}
					// Should be an octal number, so convert to octal;
					stringstream octConv;
					octConv << oct << sNumber;
					size_t octNumber;
					octConv >> octNumber;
					if(octNumber > 255){
						cerr << "Warning: octal escape sequence\
							out of range.\n";
					}
					else{
						token += static_cast<char>(octNumber);
					}
					// Adjust index;
					gi = tgi - 1;
					break;
					}
				default:
					token += grammar[gi];
			}
		}
		else switch(grammar[gi]){
			case '\\':
				escaped = true;
				containsEscaped = true;
				break;
			case '<':
				if(terminal && !quoted && !doublequoted){
					if(token.size()){
						// Add symbol to tokens and to table as terminal;
						tokens.push_back(token);
						hashAddProduction(token, GEMAP_T, vector<string>());
						token.clear();
					}
					terminal = false;
					containsEscaped = false;
				}
				token += grammar[gi];
				break;
			case '>':
				if(!terminal && !quoted && !doublequoted){
					token += grammar[gi];
					// Add symbol to tokens and to table as terminal,
					// unless it is a special token;
					tokens.push_back(token);
					if(token == "<GEXOMarker>" && eBNF){
						hashAddProduction(token, GEMAP_XO, vector<string>());
					}
					else{
						hashAddProduction(token, GEMAP_T, vector<string>());
					}
					token.clear();
					terminal = true;
					containsEscaped = false;
				}
				else{
					token += grammar[gi];
				}
				break;
			case '[':
				if(eBNF && !squarebracketed && !quoted && !doublequoted){
					if(token.size()){
						// Add symbol to tokens and to table as terminal;
						tokens.push_back(token);
						hashAddProduction(token, GEMAP_T, vector<string>());
						token.clear();
					}
					terminal = true;
					containsEscaped = false;
					squarebracketed = true;
				}
				token += grammar[gi];
				break;
			case ':':
				if(eBNF && squarebracketed){
					token += grammar[gi];
					// Build integer with previously declared value,
					// and add to repeatValues;
					string sValue = "";
					size_t ti = token.size() - 2;
					while(token[ti] != '[' && token[ti] != ':' && ti >= 1){
						sValue = token[ti] + sValue;
						ti--;
					}
					repeatValues.push_back(atoi(sValue.c_str()));
				}
				else{
					token += grammar[gi];
				}
				break;
			case ']':
				if(eBNF && squarebracketed && !quoted && !doublequoted){
					token += grammar[gi];
					// Build integer with previously declared value,
					// and add to repeatValues;
					string sValue = "";
					size_t ti = token.size() - 2;
					while(token[ti] != ':' && token[ti] != '[' && ti >= 1){
						sValue = token[ti] + sValue;
						ti--;
					}
					repeatValues.push_back(atoi(sValue.c_str()));
					// Add symbol to tokens and to table as quantifier;
					tokens.push_back(token);
					hashAddProduction(token, GEMAP_QT, vector<string>());
					// Manually set repeats on token just added;
					geMapHash &def = hDef(token);
					if(!repeatValues.size() || repeatValues.size() > 2
						|| (repeatValues.size() == 2 &&
							repeatValues[0] > repeatValues[1])){
						cerr << "Error reading quantifier from grammar.\n";
						if(repeatValues.size()){
							cerr << "Quantifier became [" << repeatValues[0];
							for(size_t ii = 0; ii < repeatValues.size(); ++ii){
								cerr << ":" << repeatValues[ii];
							}
							cerr << "].\n";
						}
						cerr << "Execution aborted.\n";
						exit(0);
					}
					def.minRepeat = repeatValues[0];
					def.maxRepeat = (repeatValues.size() == 2 ? repeatValues[1] : repeatValues[0]);
					repeatValues.clear();
					token.clear();
					terminal = true;
					containsEscaped = false;
					squarebracketed = false;
				}
				else{
					token += grammar[gi];
				}
				break;
			case '"':
				if(!quoted){
					// Bracketed strings are always separate tokens;
					doublequoted = !doublequoted;
					// Add symbol to tokens and to table as terminal;
					if(token.size()){
						tokens.push_back(token);
						hashAddProduction(token, GEMAP_T, vector<string>());
						token.clear();
						containsEscaped = false;
						terminal = true;
					}
				}
				// If quoted, just add to token;
				else{
					token += grammar[gi];
				}
				break;
			case '\'':
				if(!doublequoted){
					// Bracketed strings are always separate tokens;
					quoted = !quoted;
					// Add symbol to tokens and to table as terminal;
					if(token.size()){
						tokens.push_back(token);
						hashAddProduction(token, GEMAP_T, vector<string>());
						token.clear();
						containsEscaped = false;
						terminal = true;
					}
				}
				// If doublequoted, just add to token;
				else{
					token += grammar[gi];
				}
				break;
			case '=':
				if(!quoted && !doublequoted && !containsEscaped && token == "::"){
					token += grammar[gi];
					// Add ::= symbol to tokens and to table as GEMAP_DEF;
					tokens.push_back(token);
					hashAddProduction(token, GEMAP_DEF, vector<string>());
					token.clear();
					containsEscaped = false;
					terminal = true;
				}
				else{
					token += grammar[gi];
				}
				break;
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				if(!quoted && !doublequoted){
					// Add symbol to tokens and to table as terminal;
					if(token.size()){
						tokens.push_back(token);
						hashAddProduction(token, GEMAP_T, vector<string>());
						token.clear();
					}
					containsEscaped = false;
					terminal = true;
					// Always default to a space between symbols,
					// but avoid sequence of spaces;
					if(tokens.back() != " " || containsEscaped){
						// Add space to tokens and to table as GEMAP_SP;
						tokens.push_back(" ");
						hashAddProduction(" ", GEMAP_SP, vector<string>());
					}
				}
				else{
					token += grammar[gi];
				}
				break;
			case '|':
				// Add symbol to tokens and to table as terminal;
				if(token.size()){
					tokens.push_back(token);
					hashAddProduction(token, GEMAP_T, vector<string>());
					token.clear();
				}
				// Also add '|' to symbol table, as GEMAP_OR;
				tokens.push_back("|");
				hashAddProduction("|", GEMAP_OR, vector<string>());
				containsEscaped = false;
				terminal = true;
				break;
			case '\0':
				// Null-termination, finish tokenisation;
				gi = grammar.size();
				break;
			default:
				token += grammar[gi];
		}
		gi++;
	}
	// Add last token to tokens and to table as GEMAP_T;
	if(token.size()){
		tokens.push_back(token);
		hashAddProduction(token, GEMAP_T, vector<string>());
	}
	return true;
}

geMapHash& GEMap::hDef(const string &key){
	size_t hi, ho;
	if(getHiAndHo(key, hi, ho)){
		return hTable[hi][ho];
	}
	return blankFella;
}

///////////////////////////////////////////////////////////////////////////////
// getHiAndHo
// Searches the hash table for a definition of "key"; if found, returns true
// and sets "index" and "offset" to the index and offset of the definition; if
// not found, returns false and sets "index" and "offset" to the values where
// "key" should be inserted.
///////////////////////////////////////////////////////////////////////////////
bool GEMap::getHiAndHo(const string &key, size_t &index, size_t &offset) const{
	index = hashFn(key);
	offset = 0;
	while(offset < hTable[index].size()){
		if(!hTable[index][offset].symbolName.compare(key)){
			return true;
		}
		offset++;
	}
	return false;
}

void GEMap::clearHash(const size_t &primeIndex){
	hTable.clear();
	// Choose hash table size, based on number of symbols on grammar
	// (all references);
	if(DEBUGGING) cout << "We have what appears to be " <<  primeIndex << " symbols;";
	if(primeIndex >= MAXPRIMES){
		if(DEBUGGING) cout << " hash table size will be " << primeIndex * 2 << ".\n";
		hTable.resize(primeIndex * 2);
		return;
	}
	hTable.resize(hashPrimes[primeIndex]);
	// Resize hashtable;
	if(DEBUGGING) cout << " hash table size will be " << hTable.size() << ".\n";
}

void GEMap::hashAddProduction(const string &key, const size_t &type,
	const vector<string> &productions){
	size_t hi = 0;
	size_t ho = 0;
	// Get location of symbol in hash table;
	if(!getHiAndHo(key, hi, ho)){
		// Undefined symbol, add new definition;
		geMapHash newSymbol;
		newSymbol.symbolName = key;
		hTable[hi].push_back(newSymbol);
		ho = hTable[hi].size() - 1;
		// Default values for repetitions;
		hTable[hi][ho].minRepeat = 1;
		hTable[hi][ho].maxRepeat = 1;
	}
	if(DEBUGGING) cout << "Adding production with " << productions.size() << " symbols for '" << key <<
		"', which is a " << type << ", pos " << hi << "," << ho << ".\n";
	// Set symbol type (even if it was set before);
	hTable[hi][ho].symbolType = type;
	// Add productions only if NT symbol;
	if(hTable[hi][ho].symbolType == GEMAP_NT){
		// hi and ho now point to the location of this symbol;
		// Add vector of productions;
		hTable[hi][ho].prods.push_back(productions);
	}
}

inline size_t GEMap::hashFn(const string &input) const{
	register size_t hash = 0;
	size_t b = 378551;
	size_t a = 63689;
	for(size_t ii = 0; ii < input.length(); ++ii){
		hash = hash * a + input[ii];
		a = a * b;
	}

	/*
	for(size_t ii = 0; ii < input.size(); ++ii){
		hash = hash ^ (input[ii]<<ii);
	}
	*/

	/*
	for(size_t ii = 0; ii < input.size(); ++ii){
		hash += input[ii];
	}
	*/

	/*
	for(size_t ii = 0; ii < input.size(); ii += 2){
		hash = hash << 2;
		hash = hash ^ input[ii];
	}
	*/

	/*
	#if !defined (get16bits)
	#define get16bits(d) ((((uint32_t)(((const uint8_t *)(d))[1])) << 8)\
			       +(uint32_t)(((const uint8_t *)(d))[0]) )
	#endif
	if(input == "") return 0;

	uint32_t len = input.size();
	uint32_t hash = len, tmp;
	int rem;

	rem = len & 3;
	len >>= 2;

	// Main loop
	const char *data = &(input[0]);
	for(;len > 0; len--){
		hash  += get16bits (data);
		tmp    = (get16bits (data+2) << 11) ^ hash;
		hash   = (hash << 16) ^ tmp;
		data  += 2*sizeof (uint16_t);
		hash  += hash >> 11;
	}
	// Handle end cases
	switch(rem){
		case 3: hash += get16bits (data);
			hash ^= hash << 16;
			hash ^= data[sizeof (uint16_t)] << 18;
			hash += hash >> 11;
			break;
		case 2: hash += get16bits (data);
			hash ^= hash << 11;
			hash += hash >> 17;
			break;
		case 1: hash += *data;
			hash ^= hash << 10;
			hash += hash >> 1;
	}
	// Force "avalanching" of final 127 bits
	hash ^= hash << 3;
	hash += hash >> 5;
	hash ^= hash << 4;
	hash += hash >> 17;
	hash ^= hash << 25;
	hash += hash >> 6;
	*/
	return hash % hTable.size();
}

///////////////////////////////////////////////////////////////////////////////
// updateRecAndDepth
// Erases previous recursion and minimum depth flags of all productions in
// hash table, then goes through each of those productions and calls
// setRecAndDepth() to recalculate.
///////////////////////////////////////////////////////////////////////////////
void GEMap::updateRecAndDepth(){
	// Clear previous info;
	for(size_t hi = 0; hi < hTable.size(); ++hi){
		for(size_t ho = 0; ho < hTable[hi].size(); ++ho){
			// Set to -1, to signal undefined values;
			hTable[hi][ho].recRule = -1;
			hTable[hi][ho].minDepth = -1;
			hTable[hi][ho].recProds.clear();
			hTable[hi][ho].recProds.resize(hTable[hi][ho].prods.size(), -1);
			hTable[hi][ho].depthProds.clear();
			hTable[hi][ho].depthProds.resize(hTable[hi][ho].prods.size(), -1);
		}
	}
	// Call setRecAndDepth for each symbol;
	// Don't start with start symbol, as there might be several
	// unconnected grammars defined in hTable;
	// Cycle through hash table items;
	vector<string> callStack;
	for(size_t hi = 0; hi < hTable.size(); ++hi){
		for(size_t ho = 0; ho < hTable[hi].size(); ++ho){
			if(hTable[hi][ho].symbolName != ""){
				callStack.push_back(hTable[hi][ho].symbolName);
				setRecAndDepth(hTable[hi][ho].symbolName, callStack);
				callStack.pop_back();
				assert(hTable[hi][ho].recRule != -1 && hTable[hi][ho].minDepth != -1);
			}
		}
	}
	// Second pass;
	assert(callStack.empty());
	for(size_t hi = 0; hi < hTable.size(); ++hi){
		for(size_t ho = 0; ho < hTable[hi].size(); ++ho){
			if(hTable[hi][ho].symbolName != ""){
				callStack.push_back(hTable[hi][ho].symbolName);
				setRecAndDepth(hTable[hi][ho].symbolName, callStack);
				callStack.pop_back();
				assert(hTable[hi][ho].recRule != -1 && hTable[hi][ho].minDepth != -1);
				if(DEBUGGING)
					cout << "= 2 pass ==========================================> '" << hTable[hi][ho].symbolName
						<< "' is " << (hTable[hi][ho].recRule?"":"non-") << "recursive.\n";
				if(DEBUGGING)
					cout << "===================================================> Its minDepth is "
						<< hTable[hi][ho].minDepth << ".\n";
			}
		}
	}
}

void GEMap::setRecAndDepth(string currentSymbol, vector<string> &callStack){
	geMapHash &curSymb = hDef(currentSymbol);
	// COMMENTED: since the stack check avoids endless recursive calls,
	// symbols can be redefined in terms of recursiveness and depth,
	// which gets rid of recursive loop trouble - not as effective as
	// it could be, but not a major price to pay;
	/*
	// If symbol already defined and recursions resolved, leave;
	if(curSymb.recRule != -1 && curSymb.minDepth != -1 && curSymb.minDepth < INT_MAX - 1){
		return;
	}
	*/
	// If symbol is terminal or special symbol, set recursion to false, depth to 0, and leave;
	if(DEBUGGING) cout << "-- IN WITH " << curSymb.symbolName << ", MINDEPTH = " << curSymb.minDepth << ", TYPE = " << curSymb.symbolType << "\n";
	if(curSymb.symbolType != GEMAP_NT && curSymb.symbolType != GEMAP_QT){
		curSymb.recRule = 0;
		curSymb.minDepth = 0;
		if(DEBUGGING) cout << "-- OUT: not NT nor QT\n";
		return;
	}
	// If symbol is a quantifier, set recursion to true, depth to 0, and leave;
	// (depth of production will be set by the next associated symbol, anyway);
	if(curSymb.symbolType == GEMAP_QT){
		curSymb.recRule = 1;
		curSymb.minDepth = 0;
		if(DEBUGGING) cout << "-- OUT: QT\n";
		return;
	}
	// Default to non-recursive until proven otherwise;
	curSymb.recRule = 0;
	// Default to infinite depth;
	curSymb.minDepth = INT_MAX - 1;
	// Keep track of self-recursive productions;
	vector<size_t> selfRecursiveProds;
	// Go through each production associated with it;
	for(size_t pi = 0; pi < curSymb.prods.size(); ++pi){
		if(DEBUGGING){
			cout << "PRODUCTION " << pi << ": ";
			for(size_t ii = 0; ii < curSymb.prods[pi].size(); ++ii)
				cout << curSymb.prods[pi][ii];
			cout << "\n";
		}
		// Assume non-recursive production until proven otherwise;
		curSymb.recProds[pi] = 0;
		// Keep track of minimum depth for production;
		curSymb.depthProds[pi] = 0;
		// Go through each symbol in production;
		for(size_t si = 0; si < curSymb.prods[pi].size(); ++si){
			geMapHash &prodSymb = hDef(curSymb.prods[pi][si]);
			// It has to be defined;
			assert(prodSymb.symbolName != "");
			if(DEBUGGING) cout << "Checking " << prodSymb.symbolName << ", with mindepth = " << prodSymb.minDepth << "\n";
			if(DEBUGGING){
				cout << "CALL VECTOR:";
				for(size_t ii = 0; ii < callStack.size(); ++ii)
					cout << " " << callStack[ii];
				cout << "\n";
			}
			// Check if symbol is already in calling vector;
			vector<string>::const_iterator stackPos = find(callStack.begin(),
				callStack.end(), prodSymb.symbolName);
			if(stackPos != callStack.end()){
				if(DEBUGGING) cout << "ALREADY IN CALLING VECTOR.\n";
				// Production is recursive;
				curSymb.recRule = 1;
				curSymb.recProds[pi] = 1;
				// Check if this was a self recursion;
				if(prodSymb.symbolName == curSymb.symbolName && (!selfRecursiveProds.size() || selfRecursiveProds.back() != pi)){
					selfRecursiveProds.push_back(pi);
				}
			}
			// If any field undefined on production symbol,
			// recursively call setRecAndDepth;
			if(prodSymb.recRule == -1 || prodSymb.minDepth == -1){
				if(DEBUGGING) cout << "UNDEFINED: RECURSIVE CALL.\n";
				callStack.push_back(prodSymb.symbolName);
				setRecAndDepth(prodSymb.symbolName, callStack);
				callStack.pop_back();
			}
			// Check if symbol is flagged recursive;
			if(prodSymb.recRule == 1){
				if(DEBUGGING) cout << "FLAGGED RECURSIVE.\n";
				curSymb.recRule = 1;
				curSymb.recProds[pi] = 1;
			}
			// Update production depth (depth of highest symbol);
			if(prodSymb.minDepth > curSymb.depthProds[pi]){
				if(DEBUGGING) cout << "    updating  production depth from " << curSymb.depthProds[pi] << " to symbol depth " << prodSymb.minDepth << ".\n";
				curSymb.depthProds[pi] = prodSymb.minDepth;
			}
		}// symbols;
		// Update current symbol depth (one more than highest production);
		if(DEBUGGING) cout << "  leaving production: current symbol (top) minDepth of " << curSymb.minDepth << " becomes (from production) ";
		// FIXME: WHAT???? WHY? Why should rule mindepth become undefined, if a production has an undefined depth???
		/*
		// (if already undefined ( = INT_MAX - 1) then set to same value);
		if(curSymb.depthProds[pi] >= INT_MAX - 1){
			curSymb.minDepth = INT_MAX - 1;
		}
		else if(curSymb.depthProds[pi] + 1 < curSymb.minDepth){
			curSymb.minDepth = curSymb.depthProds[pi] + 1;
		}
		*/
		// FIXME: Replacing with just this test;
		if(curSymb.depthProds[pi] + 1 < curSymb.minDepth){
			curSymb.minDepth = curSymb.depthProds[pi] + 1;
		}
		if(DEBUGGING) cout << curSymb.minDepth << ".\n";
	}// productions;
	// Set depth of unresolved self-recursions to 1 more than main symbol;
	for(size_t pi = 0; pi < selfRecursiveProds.size(); ++pi){
		if(curSymb.depthProds[selfRecursiveProds[pi]] >= INT_MAX - 1 || curSymb.depthProds[selfRecursiveProds[pi]] < curSymb.minDepth + 1){
			curSymb.depthProds[selfRecursiveProds[pi]] = curSymb.minDepth + 1;
		}
	}
	if(DEBUGGING)
		cout << "=============================================================> NAME:" << curSymb.symbolName << "\n" <<
			"=============================================================> TYPE:" << curSymb.symbolType << "\n" <<
			"=============================================================> MIN DEPTH:" << curSymb.minDepth << "\n";
}

#endif

