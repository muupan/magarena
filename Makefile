MIN_MEM=256M
MAX_MEM=512M
JAVA=java -Xms$(MIN_MEM) -Xmx$(MAX_MEM) 
DEBUG=$(JAVA) -ea
LIBS=.:lib/annotations.jar:lib/jsr305.jar:release/lib/groovy-all-2.1.1.jar
RUN=$(JAVA) -Dcom.sun.management.jmxremote -cp $(LIBS):release/Magarena.jar
SHELL=/bin/bash
BUILD=build
SRC=$(shell find src -iname *.java)
MAG:=release/Magarena.jar
EXE:=release/Magarena.exe
NO_OUTPUT:=awk 'BEGIN{RS="\a"}{print; exit 1}'

all: tags $(MAG) $(EXE)

zips:
	make M`grep Release release/README.txt | head -1 | cut -d' ' -f2`

cubes: \
	cards/standard_all.txt \
	cards/extended_all.txt \
	cards/modern_all.txt \
	release/Magarena/mods/legacy_cube.txt \
	release/Magarena/mods/extended_cube.txt \
	release/Magarena/mods/standard_cube.txt \
	release/Magarena/mods/modern_cube.txt

themes: 
	make `ls -d1 release/Magarena/mods/*_theme | sed 's/\/$$/.zip/'`

cards_diff: $(MAG)
	for i in `hg stat -q src/magic/card release/Magarena/scripts | cut -d' ' -f2 | sort -t'/' -k4`; do hg diff $$i; done | flip -u - > $@

findbugs_warnings.txt: $(MAG)
	~/App/findbugs/bin/findbugs \
			-textui \
			-progress \
			-sortByClass \
			-emacs \
			-effort:max \
			-output $@ \
			-sourcepath src \
			build

build_warnings.txt:
	make clean all > $@

cards/legacy_banned.txt:
	curl https://www.wizards.com/Magic/TCG/Resources.aspx?x=judge/resources/sfrlegacy | grep nodec | grep -o ">[^<]*</a" | sed 's/>//g;s/<\/a//;' > $@

release/Magarena/mods/legacy_cube.txt: cards/existing_tip.txt cards/legacy_banned.txt
	join -v1 -t"|" <(sort $(word 1,$^)) <(sort $(word 2,$^)) > $@

release/Magarena/mods/%_cube.txt: cards/existing_tip.txt cards/%_all.txt
	join -t"|" <(sort $(word 1,$^)) <(sort $(word 2,$^)) > $@

cards/standard_all.out:
	touch $@
	seq 43 | parallel -q -j 20 -k wget "http://deckbox.org/games/mtg/cards?f=b31&p={}" -O - | grep "deckbox.org/mtg/" >> $@
	sed -i 's/<[^>]*>//g;s/^[ ]*//g' $@
	sed -i 's/Aether/AEther/' $@

cards/extended_all.out:
	touch $@
	seq 93 | parallel -q -j 20 -k wget "http://deckbox.org/games/mtg/cards?f=b32&p={}" -O - | grep "deckbox.org/mtg/" >> $@
	sed -i 's/<[^>]*>//g;s/^[ ]*//g' $@
	sed -i 's/Aether/AEther/' $@

cards/modern_all.out:
	touch $@
	seq 269 | parallel -q -j 20 -k wget "http://deckbox.org/games/mtg/cards?f=b35&p={}" -O - | grep "deckbox.org/mtg/" >> $@
	sed -i 's/<[^>]*>//g;s/^[ ]*//g' $@
	sed -i 's/Aether/AEther/' $@

cards/%_all.txt: cards/%_all.out
	cat $^ | unaccent utf-8 | recode html..ascii | sort | uniq > $@

cards/new.txt: cards/existing_tip.txt
	$(eval LAST := $(shell hg tags | grep "^[[:digit:]]" | head -1 | cut -d' ' -f1))
	make cards/new_$(LAST).txt
	mv cards/new_$(LAST).txt $@
	make wiki/UpcomingCards.wiki

cards/fmt.txt: cards/new.txt
	cat $^ | tr " " "@" | tr "\n" "#" | sed 's/#/, /g' | fmt | sed 's/@/ /g' > $@

changelog:
	$(eval LAST := $(shell hg tags | grep "^[[:digit:]]" | head -1 | cut -d' ' -f1))
	hg log -v | awk '{print}; /Added tag ${LAST}/ {exit 0}' > changelog

people: changelog
	grep user: $^ | sed 's/user:[ ]*//' | sort | uniq | sort > $@

cards/new_%.txt: cards/existing_tip.txt cards/existing_%.txt
	join -v1 -t"|" <(sort $(word 1,$^)) <(sort $(word 2,$^)) > $@

cards/new_scripts_%.txt: release/Magarena/scripts
	grep "name=" -h $$(hg diff -r $* | grep -B 1 "^--- /dev/null" | grep $^ | cut -d' ' -f4) | sed 's/name=//' > $@
	flip -u $@

cards/existing_scripts_%.txt: $(wildcard release/Magarena/scripts/*.txt)
	hg cat -r $* release/Magarena/scripts | grep "name=" | sed 's/.*name=//' | sort > $@
	sed -i 's/\r//' $@

cards/existing_tokens_%.txt: $(wildcard release/Magarena/scripts/*.txt)
	hg cat -r $* release/Magarena/scripts | awk -f scripts/extract_token_name.awk | sort > $@

cards/existing_%.txt: cards/existing_scripts_%.txt cards/existing_tokens_%.txt
	join -v1 -t"|" <(sort $(word 1,$^)) <(sort $(word 2,$^)) > $@

cards/groovy.txt:
	grep -ho "name=.*" `grep requires_groovy_code -lr release/Magarena/scripts` | sed 's/name=//' | sort > $@

%_full.txt: scripts/extract_candidates.awk %.txt cards/groovy.txt cards/mtg-data.txt
	awk -f $^ | sed 's/\t/\n/g'  > $@

cards/candidates_full.txt: scripts/extract_candidates.awk cards/scored_by_dec.tsv cards/unimplementable.tsv cards/mtg-data.txt
	awk -f $^ | sort -rg | sed 's/\t/\n/g' > $@

cards/unimplementable.tsv.add: cards/candidates_full.txt
	grep "|" $^ | sed 's/NAME://;s/|/\t/' >> $(basename $@)
	make $^

%.out: $(MAG)
	SGE_TASK_ID=$* exp/eval_mcts.sh
	
M1.%: clean $(EXE) cubes release/Magarena/mods/felt_theme.zip
	grep "VERSION.*1.$*" -Ir src/
	grep "Release.*1.$*" release/README.txt
	grep 1.$* -Ir Magarena.app/
	-rm -rf Magarena-1.$*
	-rm -rf Magarena-1.$*.app
	-rm Magarena-1.$*.zip
	-rm Magarena-1.$*.app.zip
	echo "preparing Lin/Win dist"
	mkdir -p Magarena-1.$*/Magarena/mods
	cp -r \
			release/gpl-3.0.html \
			release/Magarena.exe \
			release/Magarena.sh \
			release/Magarena.command \
			release/README.txt \
			release/lib \
			Magarena-1.$*
	cp -r \
			release/Magarena/avatars \
			release/Magarena/decks \
			release/Magarena/sounds \
			release/Magarena/scripts \
			Magarena-1.$*/Magarena
	cp \
			release/Magarena/mods/felt_theme.zip \
			release/Magarena/mods/*.txt \
			Magarena-1.$*/Magarena/mods
	-zip -r Magarena-1.$*.zip Magarena-1.$*
	echo "preparing Mac dist"
	cp -r Magarena.app Magarena-1.$*.app
	cd Magarena-1.$*.app/Contents; ln -s ../../Magarena-1.$* Java
	chmod a+x Magarena-1.$*.app/Contents/MacOS/MagarenaLauncher.sh
	-zip -r Magarena-1.$*.app.zip Magarena-1.$*.app

$(MAG): $(SRC)
	ant -f build.xml

tags: $(SRC)
	ctags -R src

Test%.run: $(MAG)
	$(DEBUG) -DtestGame=Test$* -Dmagarena.dir=`pwd`/release -jar $^ 2>&1 | tee Test$*.log

$(EXE): $(MAG)
	cd launch4j; ./launch4j ../release/magarena.xml

clean:
	-ant clean
	-rm -f $(MAG)

clean/%: Magarena-%.zip Magarena-%.app.zip 
	-rm -rf Magarena-$*
	-rm -rf Magarena-$*.app
	-rm Magarena-$*.zip
	-rm Magarena-$*.app.zip

log.clean:
	-rm -f *.log

inf: $(MAG)
	-while true; do make debug=true 0`date +%s`.t; done

buildhive:
	make clean games=100 ai1=MMABC ai2=MCTS `date +%s`.t
	touch cards/standard_all.out cards/extended_all.out cards/modern_all.out
	touch cards/standard_all.txt cards/extended_all.txt cards/modern_all.txt
	make zips

games ?= 10000
str1 ?= 1
str2 ?= 1
life ?= 10
ai1 ?= MMABFast
ai2 ?= MMABFast
debug ?= false
selfMode ?= false
flags ?= 

%.t: $(MAG)
	echo `hg id -n` > $*.log
	$(RUN) ${flags} \
	-Dmagarena.dir=`pwd`/release \
	-Ddebug=${debug} \
	-Dgame.log=$*.log \
	magic.DeckStrCal \
	--seed $* \
	--ai1 ${ai1} --str1 ${str1} \
	--ai2 ${ai2} --str2 ${str2} \
	--life ${life} \
	--games 1 \
	--repeat ${games} >> $*.out 2>&1

test: 100.d

debug: $(MAG)
	make 101.t debug=true games=0 || cat 101.out

%.d: $(MAG)
	$(DEBUG) -DrndSeed=$* -DselfMode=$(selfMode) -Ddebug=$(debug) -Dmagarena.dir=`pwd`/release -jar $^ |& tee $*.log

# Z = 4.4172 (99.999%)
# E = 0.01
# best estimator for r is p = h / (h + t)
# this estimator has a margin of error E, |p - r| < E at a particular Z, p - E < r < p + E
# n = Z^2 / 4E^2
#   = 48780
#   ~ 50000
%.str: $(MAG) release/Magarena/decks/JustRelentlessRats.dec release/Magarena/decks/LSK_G.dec
	$(RUN) magic.DeckStrCal --deck1 $(word 2,$^) --deck2 $(word 3,$^) --ai1 $* --ai2 $* --games 50000 > $@

#exp/%.log: $(MAG)
#	scripts/evaluate_ai.sh $* > $@

decks/dl:
	for i in `curl http://www.wizards.com/magic/magazine/archive.aspx?tag=dailydeck | grep -o mtg/daily/deck/[0-9]* | cut -d'/' -f4`; do make decks/dd_$$i.dec; done
	for i in `curl http://www.wizards.com/magic/magazine/archive.aspx?tag=topdeck | grep -o mtg/daily/td/[0-9]* | cut -d'/' -f4`; do make decks/td_$$i.dec; done
	grep "name=" -r release/Magarena/incomplete | sed 's/.*name=/100 /' > decks/with_scripts.dec

decks/with_scripts.dec: $(wildcard release/Magarena/incomplete/*.txt)
	cat release/Magarena/incomplete/*.txt | grep "name=" | sed 's/.*name=//;s/^/100 /' | sort > $@

%.fix_date:
	touch $* -d "`cat $* | head -2 | tail -1 | sed 's/# //'`"

# Daily Deck
decks/dd_%.dec:
	curl "http://www.wizards.com/Magic/Magazine/Article.aspx?x=mtg/daily/deck/$*" | awk -f scripts/dailyhtml2dec.awk > $@
	make $@.fix_date

# Top Decks
decks/td_%.dec: 
	curl http://www.wizards.com/Magic/Magazine/Article.aspx?x=mtg/daily/td/$* | awk -f scripts/dailyhtml2dec.awk > $@
	make $@.fix_date

# Mike Flores
decks/mf_%.dec:
	curl http://www.wizards.com/Magic/Magazine/Article.aspx?x=mtgcom/daily/mf$* | awk -f scripts/dailyhtml2dec.awk > $@
	make $@.fix_date

# Decks from magic-league.com
decks/ml_%.dec: scripts/apprentice2dec.awk
	wget "http://www.magic-league.com/decks/download.php?deck=$*&index=1" -O - | flip -u - | awk -f $^ > $@

# Decks from www.mtgtop8.com
decks/mtgtop8_%.dec:
	wget "http://www.mtgtop8.com/export_files/deck$*.mwDeck" -O $@

decks/update:
	$(eval LAST := $(shell curl http://mtgtop8.com/search | grep "&d=[0-9]\+" -o | sort | tail -1 | sed 's/&d=//'))
	-seq `expr ${LAST} - 200` ${LAST} | parallel make decks/mtgtop8_{}.dec
	find decks -size 0 -delete

ref/rules.txt:
	curl `wget http://www.wizards.com/magic/rules -O - | grep txt | cut -d'"' -f4` | fmt -s > $@
	flip -bu $@

resources/magic/data/icons/missing_card.png:
	convert -background gray -bordercolor black -border 5x5 -size 302x435 \
	-pointsize 30 label:'\nNo card image found\n\nSelect\n\"Download images\"\nfrom Arena menu\n\nOR\n\nSwitch to text mode\nusing the Enter key' $@

release/Magarena/mods/%_theme.zip: release/Magarena/mods/%_theme
	 zip -j $@ $^/*

cards/evan_cube.txt:
	curl http://www.cubedrafting.com/view-the-cube/ | grep jTip | sed "s/<[^>]*>//g;s/\&\#8217;/'/" > $@

cards/brett_cube.txt:
	curl http://www.snazzorama.com/magic/cube/ | grep ":WizardsAutoCard" | sed "s/<\/td>.*//;s/<[^>]*>//g;s/\&\#8217;/'/" > $@

cards/tom_cube.txt:
	wget -O - http://www.tomlapille.com/cube/tom_list.html | sed 's/<[^>]*>//g;s/^[ ]*//g;/^$$/d' > $@

cards/adam_cube.txt:
	wget -O - http://www.tomlapille.com/cube/adam_list.html | sed 's/<[^>]*>//g;s/^[ ]*//g;/^$$/d' > $@

cards/AWinnarIsYou_cube.txt:
	wget -O - http://www.tomlapille.com/cube/winnar_list.html | sed 's/<[^>]*>//g;s/^[ ]*//g;/^$$/d' > $@

cards/mtgo_cube.txt:
	wget -O - https://www.wizards.com/magic/magazine/article.aspx?x=mtg/daily/arcana/927 | grep autoCard | sed 's/<[^<]*>//g;s/^[ ]*//g' > $@

cards/mtgo_cube2.txt:
	wget -O - https://www.wizards.com/magic/magazine/article.aspx?x=mtg/daily/other/07032012d | grep autoCard | sed 's/<[^<]*>//g;s/^[ ]*//g' > $@

daily: $(EXE)
	mv $^ Magarena_`hg id -n`.exe
	scripts/googlecode_upload.py \
			-s "build `hg id -n`" \
			-p magarena \
			-u melvinzhang@gmail.com \
			-w `cat ~/Modules/notes/keys/googlecode_pw.txt` \
			-l Deprecated \
			Magarena_`hg id -n`.exe

upload/%: Magarena-%.zip Magarena-%.app.zip 
	make upload/Magarena-$*.app.zip 
	make upload/Magarena-$*.zip

upload/Magarena-%.app.zip: Magarena-%.app.zip 
	scripts/googlecode_upload.py \
			-p magarena \
			-u melvinzhang@gmail.com \
			-w `cat ~/Modules/notes/keys/googlecode_pw.txt` \
			-s "Magarena $* (Mac)" \
			-l Featured,Type-Installer,OpSys-OSX \
			$^

upload/Magarena-%.zip: Magarena-%.zip
	scripts/googlecode_upload.py \
			-p magarena \
			-u melvinzhang@gmail.com \
			-w `cat ~/Modules/notes/keys/googlecode_pw.txt` \
			-s "Magarena $*" \
			-l Featured,Type-Archive,OpSys-Linux,OpSys-Windows \
			$^

%.up: %
	scripts/googlecode_upload.py \
			-p magarena \
			-u melvinzhang@gmail.com \
			-w `cat ~/Modules/notes/keys/googlecode_pw.txt` \
			-s "$^" \
			$^

cards/scriptable.txt: scripts/analyze_cards.scala scripts/effects.txt cards/cards.xml cards/existing_tip.txt
	scala $^ > $@

grammar/rules.txt: scripts/normalize_rules.scala cards/cards.xml
	scala $^ > $@

cards/magicdraftsim-sets:
	curl www.magicdraftsim.com/card-ratings | \
	grep Kamigawa | \
	head -1 | \
	sed 's/value=/\n/g' | \
	sed 's/<.*//' | \
	cut -d\' -f2 | \
	sed '/^$$/d' > $@

cards/magicdraftsim-rating: cards/magicdraftsim-sets
	for i in `cat $^`; do \
	curl http://www.magicdraftsim.com/card-ratings/$$i | \
	pandoc -f html -t plain | \
	grep "^[ ]*[0-9]" | \
	sed "s/^[ ]*[0-9]*/$$i/;s/[ ][ ][ ]*/\t/g"; \
	done > $@

cards/current-magic-excel.txt:
	wget http://www.magictraders.com/pricelists/current-magic-excel.txt -O $@

up:
	hg pull -u
	cd wiki; hg pull -u; cd ..

code_clones:
	java -jar ~/App/simian/bin/simian-2.3.33.jar release/Magarena/scripts/*.groovy > $@

cards/mtg-data:
	curl https://dl.dropbox.com/u/2771470/index.html | grep -o 'href="mtg.*.zip' | head -1 | sed 's/href="//' | xargs -I'{}' wget https://dl.dropbox.com/u/2771470/'{}'
	unzip -j mtg-data*.zip -d cards
	rm mtg-data*.zip

github/push:
	hg gexport
	git push origin master

unique_property:
	 grep "=" release/Magarena/scripts/*.txt| cut -d'=' -f1  | sort | uniq -c | sort -n

cards/scored_by_dec.tsv: cards/existing_tip.txt cards/unimplementable.tsv $(wildcard decks/*.dec)
	./scripts/score_card.awk decks/*.dec |\
	sort -rg |\
	unaccent iso-8859-15 |\
	./scripts/keep_unimplemented.awk $(word 1,$^) /dev/stdin |\
	./scripts/keep_unimplemented.awk <(cut -f1 $(word 2,$^)) /dev/stdin > $@
	
cards/mtg_mana_costs:
	grep -ho "\(\{[^\}]\+\}\)\+" -r cards/cards.xml | sort | uniq > $@
	
cards/mag_mana_costs:
	grep -ho "\(\{[^\}]\+\}\)\+" -r src/magic/model/MagicManaCost.java release/Magarena/scripts | sort | uniq > $@

cards/mana_cost_graph.dot: cards/mtg_mana_costs
	cat $^ |\
	sed 's/{X}//g;s/{[[:digit:]]*}//g;s/{[CPQT]*}//g;/^$$/d' |\
	awk -f scripts/mana_cost_graph.awk > $@

cards/mana_cost_graph.png: cards/mana_cost_graph.dot
	circo $^ -Tpng -o $@

verify_mana_cost_order: cards/mtg_mana_costs cards/mag_mana_costs
	join -v2 $^

%.update_value: %
	if grep token= $^; then \
		echo "ERROR: Not applicable to tokens"; \
	else \
		name=$$(grep name= $^ | sed 's/name=//' | sed 's/ /%20/g');\
		value=$$(curl -sL http://gatherer.wizards.com/pages/card/details.aspx?name=$$name | grep "textRatingValue" | grep -o "[0-9]\.[^<]*" | head -1);\
		sed -i "s/value=.*/value=$$value/" $^;\
	fi \

%.normalize: %
	flip -u $^
	make $^.update_value
	vim $^
	hg add $^

find_event_data: scripts/check_data.awk
	for i in `grep "new MagicEvent(" -lr src`; do \
			grep "new Object\|data\[[0-9\]" $$i > /dev/null && echo $$i; \
			grep "new Object\|data\[[0-9\]" $$i  | awk -f $^ | sed 's/  //g' | sed 's/:/:\t/'; \
	done > $@
	flip -u $@

find_literals:
	grep "\"" src/magic/card/* | awk -f scripts/check_literals.awk

find_single_line_card_code: $(MAG)
	cat src/magic/card/*.java | sed 's/\s\+//g' | sed 's/(.*)/(...)/g' | sort | uniq -c | sort -n | grep publicstaticfinal | grep ");" > $@

find_casts: $(MAG)
	grep -n "([A-Za-z<>]\+)[A-Za-z]\+" -r src/ > $@
	flip -u $@

find_nulls: $(MAG)
	grep -n "null" -r src/ > $@
	flip -u $@

# meta check
checks: \
	check_aura \
	check_requires_groovy_code \
	check_script_name \
	check_tokens \
	check_unique_property \
	check_required_property \
	check_spells \
	check_groovy_escape \
	check_empty_return \
	check_url \
	check_image \
	check_meta \
	check_rarity \
	check_decks

# check rarity using meta.xml
check_rarity: scripts/fix_rarity.scala cards/meta.xml
	cat release/Magarena/scripts/*.txt | scala $^ | grep -v "not be L" | grep -v "Windseeker Centaur" | ${NO_OUTPUT}

# check metadata using cards.xml
check_meta: cards/scriptable.txt
	diff <(cat `grep name= $^ | sed 's/[^A-Za-z0-9]/_/g;s/name_/release\/Magarena\/scripts\//;s/$$/.txt/'`) \
	     <(sed '/^$$/d' $^) -d  |\
	grep ">" |\
	grep -v "0/0" |\
	grep -v "\*" |\
	${NO_OUTPUT}

# every url contains the string query
check_url:
	grep '^url=' -r release/Magarena/scripts | grep -v "query" | ${NO_OUTPUT}

# every image is to a jpg file or attachment
check_image:
	grep '^image=' -r release/Magarena/scripts | grep -v "jpg$$" | grep -v "png$$" | grep -v attachment.php | ${NO_OUTPUT}

# every aura must have an enchant property
check_aura:
	diff \
	<(grep "subtype.*Aura" -lr release/Magarena/scripts | sort) \
	<(grep enchant= -lr release/Magarena/scripts | sort)

# every card that requires groovy code has a corresponding groovy script file
# every groovy script file has a corresponding card script that requires groovy code
check_requires_groovy_code:
	diff \
	<(ls -1 release/Magarena/scripts/*.groovy | cut -d'/' -f 4 | sed 's/.groovy//' | sort) \
	<(grep requires_groovy_code -r release/Magarena/scripts/ | sed 's/.*=//' | sed 's/;/\n/g' | sed 's/.*scripts\///;s/.txt.*//' | sed 's/[^A-Za-z0-9]/_/g' | sort | uniq)

# $ must be escaped as \$ in groovy script
check_groovy_escape:
	grep '[^\\]\$$' -r release/Magarena/scripts | grep -v '\$${' | ${NO_OUTPUT}

# return should not be last token in groovy script
check_empty_return:
	grep "return[[:space:]]*$$" -r release/Magarena/scripts/*.groovy | ${NO_OUTPUT}

# script name is canonical card name
check_script_name:
	diff \
	<(ls -1 release/Magarena/scripts/ | grep txt | sort) \
	<(grep "name=" -r release/Magarena/scripts/ | sort | sed 's/.*name=//;s/[^A-Za-z0-9]/_/g;s/$$/.txt/')

# check tokens used match tokens in scripts
check_tokens:
	diff \
	<(grep -ho "name=.*" `grep token= -lr release/Magarena/scripts` | sed 's/name=//' | sort) \
	<(grep 'TokenCardDefinitions.get("' -r release/Magarena/scripts src | grep -o '".*"' | sed 's/"//g' | sort | uniq) | grep ">" | ${NO_OUTPUT}

# check that cards in decks exist in the game
check_decks:
	diff \
	<(grep "name=" -r release/Magarena/scripts/ | sed 's/.*name=//' | sort) \
	<(cat release/Magarena/decks/prebuilt/*.dec | grep "^[0-9]" | sed 's/^[0-9]* //' | sort | uniq) | grep ">" | ${NO_OUTPUT}

check_unique_property:
	grep "^[^=]*" -r release/Magarena/scripts/*.txt | sed 's/=.*//g' | sort | uniq -d | ${NO_OUTPUT}

property_stats:
	grep "^[a-z]*=" -hr release/Magarena/scripts/*.txt -o | sort | uniq -c | sort -n

check_required_property:
	grep ^image= -L release/Magarena/scripts/*.txt | ${NO_OUTPUT}
	grep ^name=  -L release/Magarena/scripts/*.txt | ${NO_OUTPUT}
	grep ^type=  -L release/Magarena/scripts/*.txt | ${NO_OUTPUT}
	grep ^value= -L release/Magarena/scripts/*.txt | ${NO_OUTPUT}
	grep ^rarity= -L `grep token= -L release/Magarena/scripts/*.txt` | ${NO_OUTPUT} 
	grep ^timing= -L `grep token= -L release/Magarena/scripts/*.txt` | ${NO_OUTPUT}
	grep ^url=    -L `grep token= -L release/Magarena/scripts/*.txt` | ${NO_OUTPUT}

# Instant and Sorcery must have either effect or requires_groovy_code
check_spells:
	 grep requires_groovy_code -L $$(grep effect -L $$(grep "^type.*Instant" -lr release/Magarena/scripts)) | ${NO_OUTPUT}
	 grep requires_groovy_code -L $$(grep effect -L $$(grep "^type.*Sorcery" -lr release/Magarena/scripts)) | ${NO_OUTPUT}

check_unused_filter:
	grep "Impl [A-Z]\+[^ =]*" src/magic/model/target/MagicTargetFilterFactory.java -o | sed 's/Impl //' | sort | uniq > declared-filters
	for i in `cat declared-filters`; do grep $$i -r src release/Magarena/scripts | grep -v "public static final" | grep -o $$i; done | sort | uniq > used-filters
	diff declared-filters used-filters | ${NO_OUTPUT}
	rm declared-filters used-filters

check_unused_choice:
	grep "public static final" -r src/magic/model/choice/MagicTargetChoice.java | grep -o "Choice [A-Z]\+[^ =]*" | sed 's/Choice //' | sort | uniq > declared-choices
	for i in `cat declared-choices`; do grep $$i -r src release/Magarena/scripts | grep -v "public static final" | grep -o $$i; done | sort | uniq > used-choices
	diff declared-choices used-choices | ${NO_OUTPUT}
	rm declared-choices used-choices

crash.txt: $(wildcard *.log)
	for i in `grep "^Excep" -l $^`; do \
		tail -n +`grep -n "random seed" $$i | tail -1 | cut -d':' -f1` $$i; \
	done >> $@
	rm $^

support/ui:
	for i in src/$@/*.java; do wget https://cakehat.googlecode.com/svn/trunk/$$i -O $$i; done

wiki/UpcomingCards.wiki: cards/new.txt
	echo "#summary New cards in the next release" > $@
	cat <(echo "{{{") $^ <(echo "}}}") >> $@

parser/test: $(MAG) 
	$(RUN) magic.grammar.Check < grammar/parsable.txt

parser/test_all: $(MAG) grammar/rules.txt
	$(RUN) magic.grammar.Check < $(word 2,$^)

parser/run: $(MAG)
	$(RUN) magic.grammar.Check

grammar/parsable.txt: src/magic/grammar/MagicRuleParser.java
	make parser/test_all > grammar/test_all.out
	cat grammar/test_all.out | grep PARSED | sed 's/PARSED: //' | sort | uniq > $@
	cat grammar/test_all.out | grep FAILED | sort | uniq -c | sort -n > grammar/failed.txt

src/magic/grammar/MagicRuleParser.java: grammar/mtg.peg
	java -cp lib/Mouse-1.6.jar mouse.Generate -M -G $^ -P MagicRuleParser -S MagicSyntaxTree -p magic.grammar -r magic.grammar
	mv MagicRuleParser.java $@
	sed -i 's/accept()/sem.action() \&\& accept()/g' $@

grammar/CounterType: grammar/rules.txt
	grep -o "[^ ]* counter \(on\|from\)" $@ | cut -d' ' -f1 | sort | uniq > $@
	# remove a, each, that
	# add poison

cards/cards_per_set.tsv: cards/existing_tip_full.txt
	cat <(grep -o ", [A-Z0-9]* [A-Z]" $^ | cut -d' ' -f2) \
	    <(grep -o "^[A-Z0-9]* [A-Z]"  $^ | cut -d' ' -f1) \
	| sort \
	| uniq -c \
	| sort -n \
	| sed 's/^ *//g;s/ /\t/' \
	> $@

smallest.convert:
	make `ls -1S src/magic/card | grep java |sed 's/.java//' | tail -1`.convert
	echo card with code: `ls -1 src/magic/card/*.java | wc -l`
	echo card with script: `ls -1 release/Magarena/scripts/*.groovy | wc -l`

%.convert:
	hg mv src/magic/card/$*.java release/Magarena/scripts/$*.groovy
	vim release/Magarena/scripts/$*.groovy
	sed -i 's/card_/groovy_/' release/Magarena/scripts/$*.txt
	hg commit -m "convert from java code to groovy code"

ai/benchmark.rnd:
	sort -R exp/AIs.txt > exp/rnd.txt
	ts make debug=true games=10 life=20 \
	ai1=`cat exp/rnd.txt | tail -1` \
	str1=`sort -R exp/STRs.txt | tail -1` \
	ai2=`cat exp/rnd.txt | tac | tail -1` \
	str2=`sort -R exp/STRs.txt | tail -1` \
	`date +%s`.t
	ts make ai/benchmark.rnd
	
exp/zermelo.tsv: $(wildcard exp/136*.log)
	awk -f exp/extract_games.awk $^ | ./exp/whr.rb | tac > $@

bytes_per_card.%:
	echo `hg cat -r $* release/Magarena/scripts/ | sed 's/^[[:space:]]*//' | wc -c` \
	/ \
	`hg manifest -r $* | grep 'release/Magarena/scripts/.*.txt' | wc -l` \
	| bc -l

reminder.txt: cards/cards.xml
	grep 'reminder="[^"]*"' $^ -o | sed 's/reminder=//' | sort | uniq -c | sort -rn > $@

FILES = release/Magarena/**/*.txt release/Magarena/**/*.groovy release/Magarena/decks/**/*.dec src/**/*.java

normalize_files:
	# add newline at end of file
	sed -i -e '$$a\'        ${FILES}
	# convert DOS newlines to UNIX format
	sed -i -e 's/\x0D$$//'  ${FILES}
	# convert tab to four spaces
	sed -i -e 's/\t/    /g' ${FILES}
	# remove empty lines in scripts
	sed -i -e '/^\s*$$/d' release/Magarena/scripts/*.txt
	# use mtgimage for image
	make img-mtgimage
	# use magiccard.info query for text
	make url-magiccard

%.post:
	@echo "[img]"`grep -o "http.*jpg" release/Magarena/scripts/$*.txt`"[/img]"
	@echo
	@echo "Magarena/scripts/$*.txt"
	@echo "[code]"
	@cat release/Magarena/scripts/$*.txt
	@echo "[/code]"
	@echo
	@echo "Magarena/scripts/$*.groovy"
	@echo "[code]"
	@cat release/Magarena/scripts/$*.groovy
	@echo "[/code]"

untracked:
	hg stat | sed 's/^. //;s/.*/"&"/' | xargs ls -ltr

common_actions:
	grep -ho "Magic[A-Za-z]*Action" -r release/Magarena/scripts  | sort | uniq -c | sort -n

src/magic/MurmurHash3.java:
	curl https://raw.github.com/infinispan/infinispan/master/commons/src/main/java/org/infinispan/commons/hash/MurmurHash3.java > $@
	patch $@ src/magic/MurmurHash3.diff

img-mtgimage:
	grep mtgimage -L `grep token= -L release/Magarena/scripts/*.txt` | parallel awk -f scripts/set_image.awk {} '>' {.}.img
	-ls -1 release/Magarena/scripts/*.img | parallel mv {} {.}.txt

url-magiccard:
	grep query -L `grep token= -L release/Magarena/scripts/*.txt` | parallel awk -f scripts/set_url.awk {} '>' {.}.url
	-ls -1 release/Magarena/scripts/*.url | parallel mv {} {.}.txt

incoming:
	grep -o https.* .hg/hgrc | parallel -j0 -k hg incoming {}

properties.diff:
	diff <(cat `grep name= cards/scriptable.txt | sed 's/[^A-Za-z0-9]/_/g;s/name_/release\/Magarena\/scripts\//;s/$$/.txt/'`) \
	     <(sed '/^$$/d' cards/scriptable.txt) -d -u > $@

common_costs:
	 grep "[^\"]*:" grammar/rules.txt -o | sed 's/>//;s/, /\n/g;s/://' | sort | uniq -c | sort -n | grep -v "{" > $@

github-releases.json:
	curl https://api.github.com/repos/magarena/magarena/releases > $@

correct-release-label:
	curl -XPATCH https://api.github.com/repos/magarena/magarena/releases/assets/${mac} -H"Content-Type: application/json" -d'{"name": "Magarena-${ver}.app.zip", "label":"Magarena-${ver}.app.zip for Mac"}' -u ${username}
	curl -XPATCH https://api.github.com/repos/magarena/magarena/releases/assets/${linux} -H"Content-Type: application/json" -d'{"name": "Magarena-${ver}.zip", "label":"Magarena-${ver}.zip for Linux/Windows"}' -u ${username}

push: clean normalize_files checks debug
	hg push
