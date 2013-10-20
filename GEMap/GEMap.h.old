#ifndef GEMAP_H
#define GEMAP_H

#include <string>
#include <vector>
#include <climits>
#include <map>

using namespace std;

// Initialiser types;
#define GEMAP_TOTINITIALISER 2
enum {GEMAP_RND, GEMAP_SI};
static string GEMAP_STRINITIALISER[GEMAP_TOTINITIALISER] = {"RND", "SI"};
static string GEMAP_DETAILSINITIALISER[GEMAP_TOTINITIALISER] ={
	"Random init. (remember to set minimum and maximum genome sizes)",
	"Sensible init. (remember to set max depth and tail ratio)"
	};

// Symbol types;
enum {GEMAP_NT, GEMAP_T, GEMAP_DEF, GEMAP_OR, GEMAP_SP, GEMAP_QT, GEMAP_XO, GEMAP_UNDEF};

// Parser states;
enum {GEMAP_RULEDEF, GEMAP_SIGNDEF, GEMAP_PRULE};

// Hash table items;
struct geMapHash{
	string symbolName;
	size_t symbolType;
	vector<vector<string> > prods;
	vector<int> recProds;
	vector<int> depthProds;
	int recRule;
	int minDepth;
	size_t minRepeat;
	size_t maxRepeat;
};

#define GEMAP_PARAMSSTRING		"G:W:N:z:Z:d:D:T:"
#define GEMAP_DEFAULT_GRAMMAR		"grammar.bnf"
#define GEMAP_DEFAULT_MAXWRAP		0
#define GEMAP_DEFAULT_INITIALISER	GEMAP_SI
#define GEMAP_DEFAULT_MINRNDGENOMESIZE	100
#define GEMAP_DEFAULT_MAXRNDGENOMESIZE	100
#define GEMAP_DEFAULT_SIMINDEPTH	0
#define GEMAP_DEFAULT_SIMAXDEPTH	15
#define GEMAP_DEFAULT_SITAILRATIO	0.5

class GEMap{
	public:
		GEMap();
		// Parameter methods;
		static string getParamsString();
		static string getHelpString();
		static bool paramClash(const string&);
		void scanParams(int &, char **);
		void extractParams(int &, char **, bool = true);
		string outputParams(const bool = false);
		string getGrammarFile() const;
		void setGrammarFile(const string&);
		size_t getMaxWraps() const;
		void setMaxWraps(const size_t&);
		size_t getInitialiser() const;
		void setInitialiser(const size_t&);
		string getInitialiserStr() const;
		void setInitialiserStr(const string&);
		size_t getMinRndGenomeSize() const;
		void setMinRndGenomeSize(const size_t&);
		size_t getMaxRndGenomeSize() const;
		void setMaxRndGenomeSize(const size_t&);
		size_t getSIMinDepth() const;
		void setSIMinDepth(const size_t&);
		size_t getSIMaxDepth() const;
		void setSIMaxDepth(const size_t&);
		double getSITailRatio() const;
		void setSITailRatio(const double&);
		bool readBNFFile(const string&, bool = true);
		bool readBNFString(const string&, bool = true);
		bool readEBNFFile(const string&);
		bool readEBNFString(const string&);
		bool mapGE(const vector<size_t>&, string &, size_t &,
			size_t = 1000000);
		bool mapGE(const vector<size_t>&, string &,
			size_t &, vector<size_t>&,
			size_t = 1000000);
		bool mapGE(const vector<size_t>&, string &,
			size_t &, vector<size_t>&,
			vector<string>&, size_t = 1000000, bool = true);
		bool mapGE(const vector<size_t>&, string &,
			size_t &, map<string, double> &,
			size_t = 1000000);
		bool mapGE(const vector<size_t>&, string &,
			size_t &, vector<size_t>&,
			map<string, double> &, size_t = 1000000);
		bool mapGE(const vector<size_t>&, string &,
			size_t &, vector<size_t>&,
			vector<string>&, map<string, double> &,
			size_t = 1000000, bool = true);
		bool mapGE(const vector<size_t>&, string &,
			size_t &, vector<size_t>&, vector<size_t>&,
			vector<string>&, map<string, double> &,
			size_t = 1000000, bool = true);
		bool unmapGE(const string &, vector<size_t>&);
		bool initGE(vector<size_t>&, string &, size_t &,
			const bool & = false, const size_t & = UINT_MAX);
		bool initGE(vector<size_t>&, string &, size_t &,
			const double &, const bool & = false, const size_t & = UINT_MAX);
		bool initGE(vector<size_t>&, string &, size_t &,
			vector<size_t> &, const bool & = false, const size_t & = UINT_MAX);
		bool initGE(vector<size_t>&, string &, size_t &,
			vector<size_t> &, const double &, const bool & = false,
			const size_t & = UINT_MAX);
		bool rInitGE(vector<size_t>&, string &, size_t &,
			vector<size_t> &, const double &, const bool & = false,
			const size_t & = UINT_MAX);
		bool sInitGE(vector<size_t>&, string &, size_t &,
			vector<size_t> &, const double &, const bool & = false,
			const size_t & = UINT_MAX);
		// Public attributes;
		vector<string> tokens;
		string startSymbol;
		geMapHash& hDef(const string&);
	private:
		// Parameters;
		string grammarFile;
		size_t maxWrap;
		size_t initialiser;
		size_t minRndGenomeSize;
		size_t maxRndGenomeSize;
		size_t SIMinDepth;
		size_t SIMaxDepth;
		double SITailRatio;
		bool tokenise(const string&, const bool & = true);
		// Hash table methods;
		vector<vector<geMapHash> > hTable;
		bool getHiAndHo(const string&, size_t&, size_t&) const;
		void clearHash(const size_t & = 0);
		void hashAddProduction(const string&, const size_t&,
			const vector<string>&);
		size_t hashFn(const string&) const;
		// geMapHash individual used for references not found;
		geMapHash blankFella;
		// SI methods;
		void updateRecAndDepth();
		void setRecAndDepth(string, vector<string>&);
		size_t sInitGEExpand(string&, vector<size_t>&,
			vector<size_t>&, const size_t, const size_t &,
			const bool &, string &);
};

// Operator >> : print... grammar?
// Operator << : read grammar?

#endif

