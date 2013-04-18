// qGA.h -*- C++ -*-
#ifndef _QGA_H_
#define _QGA_H_

#include <vector>
#include <utility>
#include <limits>
#include <ctime>
#include <string>
#include <utility>
#include <map>

using namespace std;

typedef vector<size_t> Genotype;
typedef vector<size_t> XOSites;

// Selector types;
#define QGA_TOTSELECTOR 2
enum {QGA_TOURNAMENT, QGA_RANK};
static string QGA_STRSELECTOR[QGA_TOTSELECTOR] = {"TOUR", "RANK"};
static string QGA_DETAILSSELECTOR[QGA_TOTSELECTOR] ={
	"Tournament selection (remember to set the tournament size)",
	"Rank selection"
	};

// Replacement types;
#define QGA_TOTREPLACEMENT 2
enum {QGA_GENERATIONAL, QGA_SSTATE};
static string QGA_STRREPLACEMENT[QGA_TOTREPLACEMENT] = {"GEN", "SS"};
static string QGA_DETAILSREPLACEMENT[QGA_TOTREPLACEMENT] ={
	"Generational replacement (remember to set elitism rate)",
	"Steady-state replacement"
	};

struct qGAIndividual{
	size_t ID;
	vector <pair<size_t, size_t> > ancestors;
	Genotype genotype;
	XOSites xoSites;
	double fitness;
	size_t effectiveSize;
	bool valid;
	// User-available flags; gets output in outputStats()
	// (this is never changed/updated/maintained by qGA)
	map<string, double> extra;
};

#define QGA_PARAMSSTRING		"p:g:s:t:f:x:w:i:a:o:r:e:m:E:M:n:X:H:h:I:c:F:S:"
#define QGA_DEFAULT_POP			10
#define QGA_DEFAULT_GENS		10
#define QGA_DEFAULT_SELECTOR		QGA_TOURNAMENT
#define QGA_DEFAULT_TSIZERATIO		0.01
#define QGA_DEFAULT_FAIRTOURNAMENT	true
#define QGA_DEFAULT_XO			0.5
#define QGA_DEFAULT_SWAP		0.0
#define QGA_DEFAULT_INTMUT		0.0
#define QGA_DEFAULT_AVGINTMUT		1.0
#define QGA_DEFAULT_EXCLUSIVEOPS	false
#define QGA_DEFAULT_REPLACEMENT		QGA_GENERATIONAL
#define QGA_DEFAULT_ELITISMRATIO	0.1
#define QGA_DEFAULT_MAXIMISE		false
#define QGA_DEFAULT_EFFECTIVEOPS	true
#define QGA_DEFAULT_MARKEDXO		false
#define QGA_DEFAULT_XONBRPOINTS		1
#define QGA_DEFAULT_SAMEPOINTXO		false
#define QGA_DEFAULT_SHCC		false
#define QGA_DEFAULT_WHCC		false
#define QGA_DEFAULT_REPLACEILLEGALS	true
#define QGA_DEFAULT_MAXCODONVALUE	numeric_limits<size_t>::max()
#define QGA_DEFAULT_XPNAME		"XP"
#define QGA_DEFAULT_SEED		static_cast<int>(time(NULL))

class qGA: public vector<qGAIndividual>{
	public:
		qGA();
		// Settings
		static std::string getParamsString();
		static std::string getHelpString();
		static bool paramClash(const std::string&);
		void scanParams(int &, char **);
		void extractParams(int &, char **, bool = true);
		string outputParams(const bool = false);
		void setParams(const qGA&);
		size_t getPopSize() const;
		void setPopSize(const size_t&);
		size_t getMaxGens() const;
		void setMaxGens(const size_t&);
		size_t getSelector() const;
		void setSelector(const size_t&);
		string getSelectorStr() const;
		void setSelectorStr(const string&);
		double getTSizeRatio() const;
		void setTSizeRatio(const double&);
		bool getFairTournament() const;
		void setFairTournament(const bool&);
		double getXORate() const;
		void setXORate(const double&);
		double getSwapRate() const;
		void setSwapRate(const double&);
		double getIntMutRate() const;
		void setIntMutRate(const double&);
		double getAvgIntMutRate() const;
		void setAvgIntMutRate(const double&);
		bool getExclusiveOps() const;
		void setExclusiveOps(const bool);
		size_t getReplacement() const;
		void setReplacement(const size_t&);
		string getReplacementStr() const;
		void setReplacementStr(const string&);
		double getElitismRatio() const;
		void setElitismRatio(const double&);
		bool getMaximising() const;
		void setMaximising(const bool&);
		bool getEffectiveOps() const;
		void setEffectiveOps(const bool);
		bool getMarkedXO() const;
		void setMarkedXO(const bool);
		size_t getXONbrPoints() const;
		void setXONbrPoints(const size_t&);
		bool getSamePointXO() const;
		void setSamePointXO(const bool&);
		bool getSHCC() const;
		void setSHCC(const bool&);
		bool getWHCC() const;
		void setWHCC(const bool&);
		bool getReplaceIllegals() const;
		void setReplaceIllegals(const bool&);
		size_t getMaxCodonValue() const;
		void setMaxCodonValue(const size_t&);
		string getXPName() const;
		void setXPName(const string&);
		int getRandomSeed() const;
		void setRandomSeed(const int&);
		// Selection
		void clearTournament();
		int tournamentSelect();
		int rankSelect();
		// Generation;
		void generateOffspring(qGA&);
		// Replacement
		void replace(const qGA&);
		void genReplacement(const qGA&, const size_t);
		void genReplacement(const qGA&);
		void ssReplacement(const qGA&);
		// Genetic Operators
		bool doCrossover(const size_t, const size_t);
		bool doMarkedCrossover(const size_t, const size_t);
		bool doCrossover(const size_t, const size_t,
			const vector<size_t> &, const vector<size_t> &);
		size_t doIntMutation(const size_t);
		size_t doAvgIntMutation(const size_t);
		// Stats
		double getSumFit() const;
		double getAvgFit() const;
		size_t getMaxFitIndex() const;
		size_t getMinFitIndex() const;
		size_t getBestFitIndex() const;
		size_t getWorstFitIndex() const;
		double getBestFit() const;
		double getWorstFit() const;
		double getAvgSize() const;
		size_t getMaxSizeIndex() const;
		size_t getMinSizeIndex() const;
		double getAvgEffSize() const;
		size_t getMaxEffSizeIndex() const;
		size_t getMinEffSizeIndex() const;
		void outputStats(const size_t &, const size_t &);
		void resetStats();
	private:
		size_t popSize;
		size_t maxGens;
		size_t selector;
		double tSizeRatio;
		bool fairTournament;
		double XORate;
		double swapRate;
		double intMutRate; // Per integer mutation rate;
		double avgIntMutRate; // Number of ints (on average) to mutate, scaled to genome length;
		bool exclusiveOps;
		size_t replacement;
		double elitismRatio;
		bool maximising;
		bool effectiveOps;
		bool markedXO;
		size_t XONbrPoints;
		bool samePointXO;
		bool shcc;
		bool whcc;
		bool replaceIllegals;
		size_t maxCodonValue;
		string XPName;
		int randomSeed;
		vector<size_t> tournamentPicks;
		// Operator stats;
		size_t XOEvents;
		size_t swapEvents;
		size_t intMutEvents;
		size_t avgIntMutEvents;
		size_t necrophiles;
		int invalidOffspring;
};

#endif

