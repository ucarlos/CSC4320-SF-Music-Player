/*
 * -----------------------------------------------------------------------------
 * Created by Ulysses Carlos on 10/28/2020 at 03:33 PM
 *
 * Tag.cc
 *
 * -----------------------------------------------------------------------------
 */

#include "./Tag.h"


//------------------------------------------------------------------------------
// Class Definitions
//------------------------------------------------------------------------------
Database_Track::Database_Track(const std::string filepath,
			       const TagLib::Tag &tag){

    tag_list[title] = tag.title().to8Bit(true);
    tag_list[artist] = tag.artist().to8Bit(true);
    
    tag_list[album_artist] = tag_list[artist]; //Copy for now
    
    tag_list[album] = tag.album().to8Bit(true);
    tag_list[genre] = tag.genre().to8Bit(true);
}


//------------------------------------------------------------------------------
// Function definitions
//------------------------------------------------------------------------------





