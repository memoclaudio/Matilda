
dwi2mask pathDwi path\mask.nii -fslgrad pathBvecs pathBbvals

dwi2response tournier pathDwi path\response.txt -fslgrad pathBvecs pathBbvals

dwi2fod csd pathDwi path\response.txt path\fod.nii -mask path/mask.nii -fslgrad pathBvecs pathBbvals

tckgen path\fod.nii path\file.tck -seed_image path\mask.nii -mask path\mask.nii -number 100000