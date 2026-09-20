#!/usr/bin/fish

git clone git@github.com:torvalds/linux.git

# for the exact dataset i used:
cd linux
git checkout 518e5b794c06c0f0eb40df3e202274a66202c137
cd ..

echo "Collecting *.c source files..."
echo "" >train.txt
for i in linux/**/*.c
    echo -ne "\r\033[KGetting $i"
    echo -e "\n\n// $i\n" >>train.txt
    cat $i >>train.txt
end
