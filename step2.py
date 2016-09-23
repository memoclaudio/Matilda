import numpy as np
from nibabel import trackvis as tv
from dipy.tracking import metrics as tm
from dipy.segment.quickbundles import QuickBundles
from dipy.io.pickles import save_pickle
from dipy.data import get_data
from dipy.viz import fvtk
import nipype.interfaces.mrtrix as mrt
import os
import sys


streams, hdr = tv.read(sys.argv[1]) 
streamlines = [i[0] for i in streams]
"""
Perform QuickBundles clustering with a 10mm distance threshold after having
downsampled the streamlines to have only 12 points.
"""

print ("Computing bundles")
qb = QuickBundles(streamlines, dist_thr=int(sys.argv[3]), pts=int(sys.argv[4])) 
print ("Completed")

"""
qb has attributes like `centroids` (cluster representatives), `total_clusters`
(total number of clusters) and methods like `partitions` (complete description
of all clusters) and `label2tracksids` (provides the indices of the streamlines
which belong in a specific cluster).
"""
centroids = qb.centroids
print(len(centroids))
streamlines = [i[0] for i in streams]
for i, centroid in enumerate(centroids):
  print (i)
  inds = qb.label2tracksids(i)
  list1 = []
  for number in range(1,len(inds)):
    list1.append((streamlines[inds[number]], None, None))
    saveTo = sys.argv[2]+'/' + `i` + '.trk'
    tv.write(saveTo, list(list1))
print("Process COMPLETED")

os.system('python /home/manu/Desktop/TESI2PARTE/tractconverter-master/scripts/WalkingTractConverter.py -i '+sys.argv[2]+' -trk2tck -o  '+sys.argv[2]+' -a '+sys.argv[5])
