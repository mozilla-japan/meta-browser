# When updating to a new version the following can be used to automate that
# a bit.
# Not automated and needs to be checked manually: languages may have been added or removed

OLD_VER=$1
NEW_VER=$2

if test -z ${OLD_VER} || test -z ${NEW_VER}; then
        echo "Usage: $0 OLD_VERSION NEW_VERSION"
        echo "  e.g.) $0 52.8.1esr 52.9.0esr"
        exit 1
fi

# # rename recipes, execute line by line
find . -name "firefox-l10n-*_${OLD_VER}.bb" > filelist
sed 's/\(.*_\)'$OLD_VER'\(.*\)/git mv \1'$OLD_VER'\2 \1'$NEW_VER'\2/' filelist > mv_files
. mv_files
rm filelist mv_files

# fetchall and update checksums
# run as a script
mkdir xpi
for f in firefox-l10n-*_${NEW_VER}.bb; do
	LANGUAGE=`echo $f | sed 's/firefox-l10n-\(.*\)_.*/\1/'`
	langUAGE=`echo ${LANGUAGE} | awk -F "-" '{printf "%s",$1; if($2 != "") {print "-" toupper($2)}}'`
	echo $langUAGE
	wget https://archive.mozilla.org/pub/firefox/releases/${NEW_VER}/linux-i686/xpi/${langUAGE}.xpi -O xpi/${langUAGE}.xpi
	sed -i '/SRC_URI\[.*sum\]/d' firefox-l10n-${LANGUAGE}_${NEW_VER}.bb
	md5sum xpi/${langUAGE}.xpi | awk '{print "SRC_URI[md5sum] = \"" $1 "\""}' >> firefox-l10n-${LANGUAGE}_${NEW_VER}.bb
	sha256sum xpi/${langUAGE}.xpi | awk '{print "SRC_URI[sha256sum] = \"" $1 "\""}' >> firefox-l10n-${LANGUAGE}_${NEW_VER}.bb
done

# create a file which when sourced bitbakes all language packages
# run in a script
echo -en bitbake "\t" > build_all
for f in firefox-l10n-*_${NEW_VER}.bb; do
	PACKAGE=`echo $f | sed 's/\(firefox-l10n-.*\)_.*/\1/'`
	echo -en $PACKAGE "\t" >> build_all
done

rm build_all
rm -rf xpi
