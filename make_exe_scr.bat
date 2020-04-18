@echo Make Snow EXE.
@call mvn install

@echo Copy Snow to Windows folder..
@copy Snow.exe %windir%\Snow.scr /y