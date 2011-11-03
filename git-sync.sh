set -e

TMP_DIR=`pwd`/tmp-data
TMP_REPO=`pwd`/jawabot
mkdir -p $TMP_DIR;

if [ ! -d $TMP_REPO ] ; then 
  mkdir -p $TMP_REPO;
  cd $TMP_REPO;
  git init;
else
  cd $TMP_REPO;
  if [ ! -d .git ] ; then echo "$TMP_REPO already exists, and is not a git repo dir."; $(exit 1); fi
fi;



##  Get SVN URL.
#if [ "" == "$1" ] ; then echo "Missing SVN URL as param #1."; $(exit 2); fi
if [ "." == "$1" ] ; then
  if [ ! -f $TMP_DIR/lastSvnUrl.txt ] ; then
    echo "ERROR: $TMP_DIR/lastSvnUrl.txt not found -> can't load last URL.";
    $(exit 1);
  else
    read -r SVN_URL < $TMP_DIR/lastSvnUrl.txt
  fi
else if [ "" != "$1" ] ; then
  SVN_URL=$1;
  echo $SVN_URL > $TMP_DIR/lastSvnUrl.txt
  svn log $SVN_URL  |  grep ' | ' | tac > $TMP_DIR/log.txt
  #r145 | dynawest@gmail.com | 2011-04-09 15:39:35 +0200 (Sat, 09 Apr 2011) | 1 line
  #r146 | ondra@dynawest.cz  | 2011-04-09 20:20:24 +0200 (Sat, 09 Apr 2011) | 1 line
else if [ ! -f $TMP_DIR/log.txt ] ; then
  echo "ERROR:  No SVN URL and no $TMP_DIR/log.txt found.";
  $(exit 1);
fi; fi; fi;

	
##  Get last processed revision.
if [ "" != "$2" ] ; then 
  REV_LAST=$2;
fi

if [ "" != "$REV_LAST" ] ; then 
  echo $REV_LAST > $TMP_DIR/lastRev.txt
  echo "LR from param: $REV_LAST"
else if [ ! -f $TMP_DIR/lastRev.txt ] ; then
  echo "ERROR:  No SVN URL in param and no $TMP_DIR/log.txt found.";
  $(exit 1);
else
  read -r REV_LAST < $TMP_DIR/lastRev.txt
  echo "LR from file: $REV_LAST"
fi; fi;


##  Main loop.
while read -r LINE ; do
  REV=`echo "$LINE" | cut -d' ' -f1`
  REV_NUM=`echo $REV | cut -b2-`
  echo "$REV_NUM" -le "$REV_LAST";
  if [ "$REV_NUM" -le "$REV_LAST" ] ; then continue; fi

  echo -e "\n\n  #####  Processing revision $REV";

  ##  Get the comment - cut first 3 lines and last 1.
  #echo "" > $TMP_DIR/comment.txt
  echo -n "[SVN "$REV"] " > $TMP_DIR/comment.txt
  svn log $SVN_URL -$REV | tail -n +4 | head -n -1 >> $TMP_DIR/comment.txt  # | tr -d '\n'
  cat $TMP_DIR/comment.txt

  ##  Before exporting, make sure to clean what is there already...
  mv $TMP_REPO/.git $TMP_DIR
  rm -rf $TMP_REPO/*
  svn export --force -$REV $SVN_URL .
  mv $TMP_DIR/.git $TMP_REPO
  git add *
  git commit -a --file $TMP_DIR/comment.txt --allow-empty
  REV_LAST=$REV_NUM;
  echo $REV_LAST > $TMP_DIR/lastRev.txt

done < $TMP_DIR/log.txt

rm -rf $TMP_DIR

##  Return to orig dir.
cd -
cp -r $TMP_REPO .
