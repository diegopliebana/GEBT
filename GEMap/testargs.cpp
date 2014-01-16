#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <iostream>
#include <string>
#undef POSIXLY_CORRECT

int main (int argc, char **argv)
{
  int aflag = 0;
  int bflag = 0;
  char *cvalue = NULL;
  int index;
  int c;

  opterr = 0;
	optind = 1;
	optopt = 0;

	std::cout << "argc = " << argc << "\nargv =";
	for(int i = 0; i < argc ; i++)
		std::cout << " " << argv[i];
	std::cout << "\n";

	std::cout << "about to read first one, optind = " << optind << "\n";
  while (optind < argc){
        c = getopt (argc, argv, "-abc:");
        if(c == -1) std::cout << "GOT -1?!?\n";
	std::cout << "apagar: opt:" << c << "\n";
	std::cout << "apagar: optind:" << optind << "\n";
	std::cout << "apagar: optopt:" << optopt << "\n";
	if(c != '?') std::cout << "apagar: optarg:" << optarg << "\n";
    switch (c)
      {
      case 'a':
        aflag = 1;
        break;
      case 'b':
        bflag = 1;
        break;
      case 'c':
        cvalue = optarg;
        break;
      case ':': std::cout << "got :\n";
      case '?':
        if (optopt == 'c')
          printf ("Option -%c requires an argument.\n", optopt);
        else if (isprint (optopt)){
          printf ("Unknown option `%c'.\n", optopt);
	std::cout << "Should push " << argv[optind - 1] << " into newargv\n";
        }
        else
          printf ("Unknown option character `\\x%x'.\n",
                   optopt);
        break;
        //return 1;
      default:
        std::cout << "Got into default behaviour (unknown stuff, not '?')\n";
	std::cout << "Should push " << argv[optind - 1] << " into newargv\n";
      }
	std::cout << "about to read another one, optind = " << optind << "\n";
  }

	std::cout << "argc = " << argc << "\nargv =";
	for(int i = 0; i < argc ; i++)
		std::cout << " " << argv[i];
	std::cout << "\n";

  printf ("aflag = %d, bflag = %d, cvalue = %s\n",
          aflag, bflag, cvalue);

  return 0;
}

