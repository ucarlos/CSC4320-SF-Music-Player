/*
 * -----------------------------------------------------------------------------
 * Created by Ulysses Carlos on 10/28/2020 at 03:30 PM
 *
 * Tag.h
 * Header file for Tag.cc, which extracts audio tags from a file and then
 * sends it to a java to c++ connector.
 *
 * In this case, java will handle whether a file exists or not, so you don't
 * have to worry about that.
 * -----------------------------------------------------------------------------
 */


#include <string>
#include <taglib/fileref.h>
#include <taglib/tag.h>
#include <taglib/tpropertymap.h>
// #include <taglib/tstring.h>
#include <cstdint>
#include <map>

enum Database_TagList {
    title = 0, artist, album_artist, album, genre
};


//------------------------------------------------------------------------------
// Function declarations
//------------------------------------------------------------------------------
std::map<enum Database_TagList, std::string> default_taglist();


class Database_Track {
public:

    // Constructors
    Database_Track()=default;
    explicit Database_Track(const std::string filepath)
	: tag_list{default_taglist()},
	  file_path{filepath} { populate_tags(); }
    
    Database_Track(const std::string filepath,
		   const std::map<enum Database_TagList,
		   std::string> &tl,
		   std::uint32_t yeer,
		   std::uint32_t track_n)
	: file_path{filepath}, tag_list{tl}, year{yeer}, track_num{track_n}
	{ populate_tags(); }

    Database_Track(const std::string filepath, const TagLib::Tag &tag);

    std::map<enum Database_TagList, std::string>& get_tag_list()
	{ return tag_list; }
    
    const std::map<enum Database_TagList, std::string>& get_tag_list() const
	{ return tag_list; }

    std::string get_file_path() const { return file_path; }
    std::uint32_t get_year() const { return year; }
    std::uint32_t get_track_num() const { return track_num; }
private:
    std::map<enum Database_TagList, std::string> tag_list;
    void populate_tags();
    friend std::ostream& operator<<(std::ostream &os, const Database_TagList &dt);
    std::string file_path{"Dummy File Path"};
    std::uint32_t year{0};
    std::uint32_t track_num{0};
    
};


void Database_Track::populate_tags(){
    TagLib::FileRef file(file_path.c_str());
    if (!file.isNull()) {
	if (!file.tag()) {
	    // Set title to file name path and exit.
	    tag_list[title] = file_path; return;	    
	}

	TagLib::Tag *tag = file.tag();
	tag_list[title] = tag->title().to8Bit(true);
	tag_list[artist] = tag->artist().to8Bit(true);
	tag_list[album] = tag->album().to8Bit(true);
	tag_list[genre] = tag->genre().to8Bit(true);

	year = tag->year();
	track_num = tag->track();
	
    }
   
}



std::ostream& operator<<(std::ostream& os, const Database_Track &dt){
    auto map = dt.get_tag_list();
    return os << dt.get_file_path() << std::endl
	      << "Track Title: " << map[title] << std::endl
	      << "Track Number: " << dt.get_track_num() << std::endl
	      << "Artist: " << map[artist] << std::endl
	      << "Album Name: " << map[album] << std::endl
	      << "Year: " << dt.get_year() << std::endl
	      << "Genre: " << map[genre] << std::endl;	    
    
}

std::map<enum Database_TagList, std::string> default_taglist() {
    static std::map<enum Database_TagList, std::string> tl;
    tl[title] = "Unknown Track";
    tl[artist] = "Unknown Artist";
    tl[album_artist] = "Unknown Album Artist";
    tl[album] = "Unknown Album";
    tl[genre] = "Unknown Genre";

    return tl;
}
