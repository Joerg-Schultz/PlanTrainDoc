---
layout: default
---

# Ziele setzen

<!-- _Be a splitter, not a lumper - (Bob Bailey, Alexandra Kurland, ...)_ -->

## Ein neues Ziel anlegen
<!-- Kinn Target -->
<video controls width="240" height="320" >
<source src="images/ZielAnlegen_KinnTarget_edited.mp4">
</video>

## Unterziele definieren

Um Ziele in kleinere Unterziele aufzubrechen, wischt man das Ziel einfach nach
und legt neue Ziele an. Dabei gibt es keine Einschränkungen, wie viele Ebenen
man einfügt.

<video controls width="240" height="320" >
<source src="images/UnterzieleAnlegen_KinnTarget.mp4">
</video>

<!-- 
für 1 min -> 10 sec, 30 sec
Hund berührt Target
Signal "Touch"
-->

## Durch Ziele navigieren

<video controls width="240" height="320" >
<source src="images/Navigation_Kinntarget_small.mp4">
</video>

# Training planen

## Wie will ich trainieren?
Um Kriterien schnell an das Verhalten des Hundes anzupassen, ist es sinnvoll,
das Training in Päckchen aufzuteilen. 
<dl>
<dt>Zeit basiert</dt>
<dd>Ich wähle die Dauer einer Trainingseinheit. Sobald ich mit der Einheit 
beginne startet PlanTrainDoc einen Timer der mich durch Vibration über den Ablauf
informiert und das Training stoppt.</dd>
<dt>Anzahl Verhalten des Tieres</dt>
<dd>Alternativ kann ich die Anzahl der Verhalten des Tieres vorgeben. Z.B. kann
ich das Training nach 5 Verhalten (ob erfolgreich oder nicht) stoppen</dd>
<dt>Frei</dt>
</dl>

## Was will ich trainieren

Menschen sind schlecht darin, sich zufällig zu verhalten. Das ist ein Problem,
wenn ich ein Kriterium variabel halten will. Genau hier kann ein Computer helfen,
indem er mir als Trainer variable Kriterien vorgibt. PlanTrainDoc verwendet für 
Distanz und Dauer Zahlenreihen, die auf Morgan Spectors Buch 
[Clicker Training for Obedience](https://www.amazon.de/Clicker-Training-Obedience-Shaping-Performance-Positively/dp/0962401781/ref=sr_1_2?__mk_de_DE=%C3%85M%C3%85%C5%BD%C3%95%C3%91&dchild=1&keywords=Clicker+training+for+obedience&qid=1630042590&sr=8-2)
basieren. 
<dl>
<dt>Dauer</dt> 
<dd>Hier lege ich die mittlere Dauer eines Versuches fest. Während des 
Trainings gibt mir PlanTrainDoc dann unterschiedliche Zeiten um diesen
Mittelwert vor. Von 0,1 bis 1,5 Sekunden wird eine konstante Zeit vorgegeben,
danach werden sie variiert. [Werte](durationscheme.txt)</dd>
<dt>Distanz</dt>
<dd>Hier lege ich als Kriterium eine mittlere Distanz fest. Wie bei der Dauer
gibt mir PlanTrainDoc während des Trainings eine Distanz vor die sich variabel um
diesen Mittelwert bewegt. [Werte](distancescheme.txt)</dd>
<dt>Signal unterscheiden</dt>
<dd>Bei der Signalunterscheidung (z.B. Platz vs. Sitz aus dem Stehen) soll
der Hund nur das Signal als Unterscheidungskriterium haben und nicht eine
Reihenfolge der Signale. In PlanTrainDoc kann ich eine beliebige Anzahl von
Signalen vorgeben. Währendes Trainings wird dann gleichverteilt aus diesen
Signalen gezogen.</dd>
<dt>Signal einführen</dt>
<dd>Unter Signalkontrolle soll der Hund das Verhalten erst dann zeigen, wenn 
das Signal gegeben wird. Eine Möglichkeit, dies zu trainieren, ist, den Hund auch
für das Warteverhalten zu belohnen. Mit PlanTrainDoc kann ich mir vorgeben
lassen, wie oft ich das Warteverhalten belohne bzw. das Signal gebe.</dd>
<dt>Frei</dt>
<dd></dd>
</dl>

# Trainieren und Dokumentieren

Im Training verwende ich PlanTrainDoc als Clicker (einstellbar). Da ich
zusätzlich auch jeden "Fehler“ (neutraler: Reset) notiere, habe ich jederzeit
während des Trainings die genaue Erfolgsrate.

Je nach gewählter Trainingsart gibt mir PlanTrainDoc hier Kriterien vor. In den
Einstellungen kann ich wählen, ob mir das jeweilige Kriterium angesagt oder
nur angezeigt wird.

Vor jeder Trainingseinheit schreibe ich das aktuelle Kriterium auf,
nach der Einheit dokumentiere ich direkt den Erfolg der Einheit. Damit erstelle
ich während des Trainings direkt in PlanTrainDoc meine Dokumentation. 

# Auswerten

PlanTrainDoc merkt sich jedes erfolgreiche (Click) und nicht erfolgreiche (Reset)
Verhalten. Damit kann ich den Erfolg jedes Trainings, jedes
Unterziels und Ziels überprüfen. 

