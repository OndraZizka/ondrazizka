set VERSION=1.5.0

wget http://www.jibble.org/files/pircbot-%VERSION%.zip

@rem ##  Trick to make independent of the name of the dir inside the archive.
@rem #unzip -d X *.zip
@rem #mv X/* bot


unzip pircbot-%VERSION%.zip

pushd .
cd pircbot-%VERSION%\javadoc\
zip ..\..\javadoc.zip *
popd

cmd /C mvn install:install-file -Djavadoc=javadoc.zip -DpomFile=pircbot-pom.xml -Dpackaging=jar -DgeneratePom=true -DgroupId=cz.dynawest.third.pircbot -Dfile=pircbot-%VERSION%\pircbot.jar -DartifactId=PircBot -Dversion=%VERSION%


