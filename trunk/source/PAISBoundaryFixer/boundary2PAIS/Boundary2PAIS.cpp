#include <stdio.h>
#include <string>
#include <iostream>
#include <fstream>
#include <fts.h>

using namespace std;

string outpath;

int proc_contours_txt(string pathname,string filename)
{
	ifstream infile;
	infile.open(pathname.c_str());
	ofstream outfile;
    string outfullpath = outpath + filename.substr(0,filename.size()-3) + "txt";
    outfile.open(outfullpath.c_str());
    cout << outfullpath.c_str() << endl;

	int i=0,j=0;
	int in_ctr = 0;

	string str,xp,yp;
	int pos1,pos2,eol_ch;

	outfile << "#CalculationMetadata" << endl;
	outfile <<	"AREA, area, DOUBLE" << endl;
	outfile << "PERIMETER, perimeter, DOUBLE" << endl;
	outfile << "ECCENTRICITY, eccentricity, DOUBLE" << endl;
	outfile << "CIRCULARITY, circularity, DOUBLE" << endl;
	outfile << "MAJOR_AXIS, the length of the major axis of the fitted ellipse, DOUBLE" << endl;
	outfile << "MINOR_AXIS, the length of the minor axis of the fitted ellipse, DOUBLE" << endl;
	outfile << "EXTENT_RATIO, extent ratio, DOUBLE" << endl;
	outfile << "MEAN_INTENSITY, mean intensity, DOUBLE" << endl;
	outfile << "MAX_INTENSITY, max intensity,  DOUBLE" << endl;
	outfile << "MIN_INTENSITY, min intensity, DOUBLE" << endl;
	outfile << "STD_INTENSITY, std intensity, DOUBLE" << endl;
	outfile << "ENTROPY, entropy,  DOUBLE" << endl;
	outfile << "ENERGY, energy, DOUBLE" << endl;
	outfile << "SKEWNESS, skewness, DOUBLE" << endl;
	outfile << "KURTOSIS, kurtosis, DOUBLE" << endl;
	outfile << "MEAN_GRADIENT_MAGNITUDE, mean of magnitude of gradient, DOUBLE" << endl;
	outfile << "STD_GRADIENT_MAGNITUDE, std of magnitude of gradient,DOUBLE" << endl;
	outfile << "ENTROPY_GRADIENT_MAGNITUDE, entropy of magnitude of gradient, DOUBLE" << endl;
	outfile << "ENERGY_GRADIENT_MAGNITUDE, energy of magnitude of gradient, DOUBLE" << endl;
	outfile << "SKEWNESS_GRADIENT_MAGNITUDE, skewness of magnitude of gradient, DOUBLE" << endl;
	outfile << "KURTOSIS_GRADIENT_MAGNITUDE, kurtosis of magnitude of gradient, DOUBLE" << endl;
	outfile << "SUM_CANNY_PIXEL, sum of number of canny pixel, DOUBLE" << endl;
	outfile << "MEAN_CANNY_PIXEL, normalized number of canny pixel, DOUBLE" << endl;
	outfile << "CYTO_H_MeanIntensity, mean of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_MeanMedianDifferenceIntensity, the difference between CYTO_H_MeanIntensity and the median of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_MaxIntensity, max pixel intensity in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_MinIntensity, min pixel intensity in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_StdIntensity, std pixel intensity in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_Entropy, entropy of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_Energy, energy of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_Skewness, skewness of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_Kurtosis, kurtosis of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_MeanGradMag, mean gradient magnitude of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_StdGradMag, std gradient magnitude of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_EntropyGradMag, entropy of gradient magnitude of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_EnergyGradMag, energy of gradient magnitude of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_SkewnessGradMag, skewness of gradient magnitude of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_KurtosisGradMag, kurtosis of gradient magnitude of intensities of pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_SumCanny, sum of number of canny pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_H_MeanCanny, normalized number of canny pixels in cytoplasmic regions from hematoxylin image component, DOUBLE" << endl;
	outfile << "CYTO_E_MeanIntensity, mean of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_MeanMedianDifferenceIntensity, the difference between CYTO_E_MeanIntensity and the median of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_MaxIntensity, max pixel intensity in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_MinIntensity, min pixel intensity in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_StdIntensity, std pixel intensity in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_Entropy, entropy of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_Energy, energy of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_Skewness, skewness of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_Kurtosis, kurtosis of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_MeanGradMag, mean gradient magnitude of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_StdGradMag, std gradient magnitude of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_EntropyGradMag, entropy of gradient magnitude of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_EnergyGradMag, energy of gradient magnitude of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_SkewnessGradMag, skewness of gradient magnitude of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_KurtosisGradMag, kurtosis of gradient magnitude of intensities of pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_SumCanny, sum of number of canny pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_E_MeanCanny, normalized number of canny pixels in cytoplasmic regions from eosin image component, DOUBLE" << endl;
	outfile << "CYTO_G_MeanIntensity, mean of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_MeanMedianDifferenceIntensity, the difference between CYTO_G_MeanIntensity and the median of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_MaxIntensity, max pixel intensity in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_MinIntensity, min pixel intensity in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_StdIntensity, std pixel intensity in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_Entropy, entropy of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_Energy, energy of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_Skewness, skewness of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_Kurtosis, kurtosis of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_MeanGradMag, mean gradient magnitude of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_StdGradMag, std gradient magnitude of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_EntropyGradMag, entropy of gradient magnitude of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_EnergyGradMag, energy of gradient magnitude of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_SkewnessGradMag, skewness of gradient magnitude of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_KurtosisGradMag, kurtosis of gradient magnitude of intensities of pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_SumCanny, sum of number of canny pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;
	outfile << "CYTO_G_MeanCanny, normalized number of canny pixels in cytoplasmic regions from grayscale image component, DOUBLE" << endl;

	outfile << "#ObservationMetadata" << endl;
	outfile << "Nuclei Score, grade, Ordinal, Integer" << endl;

	outfile << "#Markups format=db2" << endl;
	while (infile.good())
	{
		infile >> str;
//		cout << str << endl;
		pos1 = str.find(',');
		pos2 = str.size() - 1;
		eol_ch = infile.peek();
		if (pos1 != pos2 && pos1>0)
		{
			outfile << " " << str.substr(0,pos1) << "," << str.substr(pos1+1,pos2-pos1);
		}
		if (eol_ch==10 || eol_ch==13)
		{
			outfile << endl;
		}
	}

	infile.close();
	outfile.close();
}


int main(int argc,char * argv[])
{
	if (argc < 3)
	{
		cout << "Usage: ./Boundary2PAIS [/infilepath/subfolder/] [/outfilepath/subfolder/]" << endl;
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
			if (filename.substr(filename.size()-3,3)=="txt")
			{
				proc_contours_txt(pathname,filename);
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
