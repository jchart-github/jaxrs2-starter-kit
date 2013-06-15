if %1"" EQU "" goto usage
if %1 EQU build goto build
if %1 EQU book-server goto book-server
if %1 EQU book-client goto book-client
goto usage

:book-server
pushd book-server
call mvn jetty:run
goto end

:book-client
pushd book-client
call mvn test
goto end

:build
pushd common-dto
call mvn package install
popd
pushd book-client
call mvn clean dependency:copy-dependencies
popd
pushd book-server
call mvn clean dependency:copy-dependencies
popd
goto end

:usage
echo USAGE:  %0 [build ^|book-server ^| book-client ^|]
goto end

:end
popd
