#!/usr/bin/perl

$dwiFile=$ARGV[0];
$bvecsFile=$ARGV[1];
$bvalsFile=$ARGV[2];
$numberOfTracks=$ARGV[3];
$fileTck=$ARGV[4];
$path="output_tck";

qx(
dwi2mask $dwiFile $path/mask.nii -fslgrad $bvecsFile $bvalsFile
echo dwi2maskComplete
dwi2response tournier $dwiFile $path/response.txt -fslgrad $bvecsFile $bvalsFile

dwi2fod csd $dwiFile $path/response.txt $path/fod.nii -mask $path/mask.nii -fslgrad $bvecsFile $bvalsFile 

tckgen $path/fod.nii $path/$fileTck.tck -seed_image $path/mask.nii -mask $path/mask.nii -number $numberOfTracks

unlink $path/fod.nii
unlink $path/mask.nii
unlink $path/response.txt

mrview $dwiFile -tractography.load $path/$fileTck.tck
)

