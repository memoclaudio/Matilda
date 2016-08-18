#!/usr/bin/perl

print "$ARGV[0]";
$mrtrixFolder=$ARGV[0];
$dwiFile=$ARGV[1];
$bvecsFile=$ARGV[2];
$bvalsFile=$ARGV[3];

qx(
$mrtrixFolder/release/bin/dwi2mask $dwiFile mask.nii -fslgrad $bvecsFile $bvalsFile -force

$mrtrixFolder/scripts/dwi2response tournier $dwiFile response.txt -fslgrad $bvecsFile $bvalsFile -force

$mrtrixFolder/release/bin/dwi2fod csd $dwiFile response.txt fod.nii -mask mask.nii -fslgrad $bvecsFile $bvalsFile -force

$mrtrixFolder/release/bin/tckgen fod.nii file.tck -seed_image mask.nii -mask mask.nii -number 100 -force

$mrtrixFolder/release/bin/mrview $dwiFile -tractography.load file.tck
)

