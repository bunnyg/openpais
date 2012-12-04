//============================================================================
// Name        : BoundaryFix.cpp
// Author      : bkatigb
// Version     :
// Copyright   :
// Description :
//============================================================================

#include "clipper.h"
#include <iomanip>
#include <iostream>
#include <cstdlib>
#include <cmath>
#include <stdio.h>
#include <ios>
#include <map>
#include <fstream>
#include <vector>
#include <list>
#include <set>
#include <fts.h>
#include <sstream>

#include <boost/assign.hpp>


#include <boost/geometry/geometry.hpp>
#include <boost/geometry/geometries/geometries.hpp>
#include <boost/geometry/geometries/polygon.hpp>
#include <boost/geometry/geometries/adapted/boost_tuple.hpp>

#include <boost/geometry/geometries/linestring.hpp>
#include <boost/geometry/geometries/point_xy.hpp>

 BOOST_GEOMETRY_REGISTER_BOOST_TUPLE_CS(cs::cartesian)

#include <boost/assign.hpp>

using namespace std;
using namespace ClipperLib;
using namespace boost::geometry;

struct points
    {
        float x;
        float y;
    };


vector<points> coord;


/*data id*/
   string file_id;
   string rec_id;
   int    orig_item_cnt,item_out_cnt;

	string str,xp,yp,sv_rec_id;
	string infilename,infilepath,infilefn;
	string outpath;



   ifstream infile;
   ofstream outfile;
   ofstream logfile;


void process()
{
    stringstream s1;

	string infullpath = infilepath + infilename;
	infile.open(infullpath.c_str());
	string outfullpath = outpath.c_str() + infilefn.substr(0,infilefn.size()-3) + "txt";
    outfile.open(outfullpath.c_str());
    logfile << infullpath.c_str() << endl;
    logfile << outfullpath.c_str() << endl;

	using boost::assign::tuple_list_of;
	using boost::make_tuple;
	using boost::geometry::append;
	typedef boost::geometry::model::polygon<boost::tuple<int, int> > polygon;
	typedef boost::geometry::model::polygon
		<
			boost::tuple<int, int>
		> clockwise_closed_polygon;

	clockwise_closed_polygon cwcp,cwcp_in;
	double area_after;
	double area_before;

	typedef boost::geometry::model::d2::point_xy<double> xy;
	boost::geometry::model::linestring<xy> line;
	boost::geometry::model::linestring<xy> simplified;

	typedef boost::geometry::model::d2::point_xy<double> P;
	boost::geometry::model::linestring<P> line1, line2;

	int pos1,pos2,eol_ch;
	bool f1st=true;

	typedef boost::geometry::model::d2::point_xy<double> point_type;
	typedef boost::geometry::model::polygon<point_type> polygon_type;
	point_type p(4, 1);

	IntPoint XY;
	Polygon  poly_in;
	Polygons poly_out1;
	Polygons poly_out;
	polygon poly;
	points pt;

	f1st = true;
	int ctr0=0;
	int ctr1=0;
	int ctr2=0;
	int ln_ctr=0;


	bool b = false;
	bool proc = false;
	cwcp.clear();
	while (infile.good())
	{
		infile >> str;
		if (f1st)
		{
			pos1 = str.find(';');
			if (pos1 > 0)
			{
			sv_rec_id = str;
			}
//			f1st = false;
			ctr0 = 0;
		}
		pos1 = str.find(',');
		pos2 = str.size() - 1;
		eol_ch = infile.peek();
		if (f1st)
		{
			f1st = false;
		}
		else
		{
			if (pos1 != pos2 && pos1>0)
			{
				xp    = str.substr(0,pos1);
				yp    = str.substr(pos1+1,pos2-pos1);
				pt.x  = strtod(xp.c_str(),NULL);
				pt.y  = strtod(yp.c_str(),NULL);
				coord.push_back(pt);
				ctr0 = coord.size();
			}
		}
		if (eol_ch==10 || eol_ch==13)
		{
			rec_id        = sv_rec_id;
			file_id       = infilename.c_str();
			ctr0 = coord.size();

			if (ctr0 >= 4)
			{
				proc = true;
			}
			else
			{
				proc = false;
			}

			if (proc)
			{
				/*remove collinear*/
				boost::geometry::clear(line1);
				boost::geometry::clear(line2);
				for (int i=0;i!=ctr0;i++)
				{
					append(line1, make_tuple(coord[i].x, coord[i].y));
				}
				if (ctr0 > 500)
				{
					boost::geometry::simplify(line1, line2, 0.1);
				}
				else if (ctr0 > 200)
				{
					boost::geometry::simplify(line1, line2, 0.25);
				}
				else if (ctr0 < 50)
				{
					boost::geometry::simplify(line1, line2, 2.0);
//					boost::geometry::simplify(line1, line2, 0.0);
				}
				else
				{
					boost::geometry::simplify(line1, line2, 1.75);
				}

				ctr0 = line2.size();
				if (ctr0 >= 4)
				{
					proc = true;
				}
				else
				{
					proc = false;
				}
			}

			if (proc)
			{
				poly_in.clear();
				poly_out.clear();
				/*splits up in the event of self-intersect*/
				for (int i = 0;i!=ctr0;i++)
				{
					XY.X = line2.at(i).get<0>();
					XY.Y = line2.at(i).get<1>();
					poly_in.push_back(XY);
//					cout << XY.X << "," << XY.Y << " ";
				}

				SimplifyPolygon(poly_in,poly_out1);
//				cout << endl << "poly_out1.size()" << poly_out1.size() << endl;
				if (poly_out1.size() <=  1)
				{
					poly_out = poly_out1;
				}
				else
				{
					SimplifyPolygons(poly_out1,poly_out);
					poly_out = poly_out1;
				}

				ctr1 = poly_out.size();
//                cout << "ctr1: " << ctr1 << endl;
				for (int i=0;i<ctr1;i++)
				{
//					cout << "# " << i << " output size " << poly_out[i].size() << endl;
					if (poly_out[i].size()>=3)
					{
						ctr2 = poly_out[i].size();
						boost::geometry::clear(cwcp);

						for (int j=0;j!=ctr2;j++)
						{
							 boost::geometry::exterior_ring(cwcp).push_back(make_tuple(poly_out[i][j].X,poly_out[i][j].Y));
						}

						boost::geometry::correct(cwcp);
						outfile << rec_id;
						outfile << dsv(cwcp,","," ","","","","") << endl;
					}
				}
			}

			f1st = true;
			coord.clear();
		} /*eol*/

	} /*while good*/

	coord.clear();

	infile.close();
	outfile.close();

}

int main(int argc,char * argv[]) {

//	cout << "argc: " << argc << " " <<  argv[1] << " " <<  argv[2] << endl;
	if (argc < 3)
	{
		cout << "Usage: ./BoundaryFix [/infilepath/subfolder/] [/outfilepath/subfolder/]" << endl;
		return 9;
	}
	if (argc >= 3)
	{
	  if (argv[2] ==  argv[3])
	  {
		  cout << "Please specify different output folder" << endl;
	      return 9;
	  }
	  infilepath = argv[1];
	  outpath    = argv[2];
	}

	string logfn = outpath + "logfile.out";
	logfile.open(logfn.c_str());

	char * DIR = argv[1];
	chdir(DIR);
	char *dot[] = {".",0};
	char **paths = dot;
	string filename,pathname;
	FTS *tree = fts_open(paths, FTS_NOCHDIR, 0);
	if (!tree)
	{
		perror("fts_open");
		return 1;
	}

	FTSENT *node;
	while ((node = fts_read(tree)))
	{
		if (node->fts_level > 0 && node->fts_name[0] == '.')
			fts_set(tree, node, FTS_SKIP);
			else if (node->fts_info & FTS_F)
			{
				filename = node->fts_name;
				pathname = node->fts_path;
				if (filename.substr(filename.size()-3,3)=="txt")
				{
					infilefn   = node->fts_name;
					infilename = node->fts_name;
					process();
				}
			}
		}
    if (fts_close(tree))
    {
        perror("fts_close");
        return 1;
    }

    logfile.close();
    return 0;
}
