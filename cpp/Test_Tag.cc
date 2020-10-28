/*
 * -----------------------------------------------------------------------------
 * Created by Ulysses Carlos on 10/28/2020 at 06:14 PM
 *
 * Test_Tag.cc
 *
 * -----------------------------------------------------------------------------
 */

#include <cstdlib>
#include <filesystem>
#include <iostream>
#include "./Tag.h"
using namespace std;

int main(int argc, char *argv[]){
    if (argc < 2){
	cerr << "Usage: ./Test_Tag [File_path]\n";
	exit(EXIT_FAILURE);
    }

    if (!std::filesystem::exists(argv[1])){
	cerr << "Error: File \"" << argv[1] << "\" does not exist.\n";
	exit(EXIT_FAILURE);
    }

    // Now populate!
    Database_Track dt{argv[1]};
    cout << dt << endl;
    
	

}
