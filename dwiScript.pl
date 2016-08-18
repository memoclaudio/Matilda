#!/usr/bin/perl

print "$ARGV[0]";
$mrtrixFolder=$ARGV[0];

qx(
/home/manu/mrtrix3/release/bin/dwi2mask dwi.nii mask.nii -fslgrad bvecs bvals -force

/home/manu/mrtrix3/scripts/dwi2response tournier dwi.nii response.txt -fslgrad bvecs bvals -force

/home/manu/mrtrix3/release/bin/dwi2fod csd dwi.nii response.txt fod.nii -mask mask.nii -fslgrad bvecs bvals -force

/home/manu/mrtrix3/release/bin/tckgen fod.nii file.tck -seed_image mask.nii -mask mask.nii -number 100 -force

/home/manu/mrtrix3/release/bin/mrview dwi.nii -tractography.load file.tck
)

