#include <stdio.h>
#include <string>
#include <iostream>
#include <fstream>
#include <opencv2/opencv.hpp>
#include <fts.h>

using namespace std;
using namespace cv;

string outpath;

int proc_contours_pbm(string pathname,string filename)
{
	ofstream outfile;
    string outfullpath = outpath + filename.substr(0,filename.size()-3) + "txt";
    outfile.open(outfullpath.c_str());

    cout << outfullpath.c_str() << endl;

	int i=0,j=0;
	int in_ctr = 0;

	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
    Mat src,src_gray,dst;
    src = imread(pathname.c_str(),1);
    cvtColor( src, src_gray, CV_RGB2GRAY );
    threshold( src_gray, dst,0,255,0);
    findContours(dst, contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);

    if (contours.size() > 0)
    {
    	printf("found contours %i \n",contours.size());
    }
    else
    {
    	printf("no contours \n");
    }

    int idx1=0,idx2=0;
    idx1 = contours.size();
    string fn,pth;
    fn  = filename;
    pth = pathname;

     for (i=0;i<idx1;i++)
    {
    	idx2 = contours.at(i).size();
    	int x=idx2;
    	double * y=NULL;
    	double * z=NULL;
    	y = new double[x];
    	z = new double[x];
    	for (j=0;j<idx2;j++)
    	{
            y[j] = contours.at(i).at(j).x;
            z[j] = contours.at(i).at(j).y;
    	}

       	if (j>3)
       	{
       		in_ctr++;
       		outfile << in_ctr << ";";
       		for (int ij=0;ij!=j;ij++)
       		{
       			outfile << " " << y[ij] << "," << z[ij];
       		}
       		outfile << endl;
    	}

        delete [] y;
        y = NULL;
        delete [] z;
        z = NULL;
    }
}


int main(int argc,char * argv[])
{
	if (argc < 3)
	{
		cout << "Usage: ./BoundaryConvPBM [/infilepath/subfolder/] [/outfilepath/subfolder/]" << endl;
		return 9;
	}
	if (argc >= 3)
	{
	  if (argv[2] ==  argv[3])
	  {
		  cout << "Please specify different output folder" << endl;
		  return 9;
	  }
	  outpath    = argv[2];
	}

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
			if (filename.substr(filename.size()-3,3)=="pbm")
			{
				printf("binary image input: %s \n", node->fts_name);
				proc_contours_pbm(pathname,filename);
			}
		}
	}
    if (fts_close(tree))
    {
        perror("fts_close");
        return 1;
    }
    return 0;

	}
