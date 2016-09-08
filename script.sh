#!/bin/sh

dwiFile=$1;
bvecsFile=$2;
bvalsFile=$3;
numberOfTracks=$4;
fileTck=$5;
path="output_tck";

dwi2mask $dwiFile $path/mask.nii -fslgrad $bvecsFile $bvalsFile

echo "step1\n";

dwi2response tournier $dwiFile $path/response.txt -fslgrad $bvecsFile $bvalsFile

echo "step2\n";

dwi2fod csd $dwiFile $path/response.txt $path/fod.nii -mask $path/mask.nii -fslgrad $bvecsFile $bvalsFile

echo "step3\n";

tckgen $path/fod.nii $path/$fileTck.tck -seed_image $path/mask.nii -mask $path/mask.nii -number $numberOfTracks

echo "step4\n";

unlink $path/fod.nii
unlink $path/mask.nii
unlink $path/response.txt

mrview $dwiFile -tractography.load $path/$fileTck.tck

echo "step5\n";
