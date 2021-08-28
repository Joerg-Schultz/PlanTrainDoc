---
layout: default
---

# Warum eine App?

Beim Training mit meinem Hund bin ich immer wieder in die gleichen Probleme gelaufen.

- Während des Trainings kann ich mir nicht gleichzeitig merken, 
  wie viele erfolgreiche und fehlerhafte Verhalten der Hund gezeigt hat.
- Wenn ich jeweils nur eine Minute pro Einheit trainieren will, vergesse ich
  spätestens bei der dritten Einheit, den Wecker zu starten. Will ich nur eine
  bestimmte Anzahl von Versuchen trainieren, bin ich mir am Ende der Einheit nicht sicher,
  ob der Hund schon fünf oder doch nur vier Versuche gezeigt hat.
- Um mit variablen Kriterien zu arbeiten, muss ich während des Trainings auf Zetteln
  die jeweiligen Werte nachvollziehen und kann mich nicht auf den Hund konzentrieren.
- Wenn ich während des Trainings dokumentiere, wird die Einheit immer mehr 
  zerstückelt, weil ich ständig zum Rechner / Papier gehen muss.
- Beim Training draußen kann ich nicht mit Stift und Zettel arbeiten.
- Wenn ich vor dem Training einen Plan erstellt habe, muss ich das Training 
  komplett unterbrechen, wenn ich auch nur kleine Änderungen am Plan vornehmen will.

Nachdem mir klar geworden ist, dass dadurch mein Training nicht so gut ist, wie es sein könnte,
habe ich angefangen 
[PlanTrainDoc](https://play.google.com/store/apps/details?id=de.tierwohlteam.android.plantraindoc) 
zu entwickeln. Inzwischen verwende ich die App bei allen Schritten meines Trainings – angefangen
von der Planung über das eigentliche Training, bei dem ich gleichzeitig meine Dokumentation erstelle,
bis zur Auswertung, anhand der ich wieder den Trainingsplan anpasse. Während des Trainings zeigt mir
PlanTrainDoc an, wie viele Versuche der Hund gemacht hat und wie viele davon erfolgreich waren. 
Die App sagt mir Bescheid, wann ich mein Training stoppen will.
Wenn ich mit variablen Kriterien arbeite, sagt mir PlanTrainDoc für jeden Versuch den aktuellen
Wert an. Vor dem Training lege ich in der App meine Ziele fest und passe sie während des
Trainings flexibel an. Für jedes Ziel kann ich mirErfolgsstatistiken anzeigen lassen und 
meinen Plan verbessern. Und da das ganze auf meinem Smartphone läuft, habe ich alle Informationen
immer bei mir.

# Was kann ich mit der App machen?

## Ziele festlegen

### Ein neues Ziel anlegen

Als ersten Schritt lege ich in der App ein neues Ziel an. Dafür clicke ich
auf den orangen '+' Button. Für jedes Ziel gebe ich einen Titel an,
definiere Details des Verhaltens und lege den Status des Ziels fest. Mit dem orangen 
'Disketten' Button speichere ich das Ziel. Möchte ich Änderungen vornehmen, z. B. den Status auf
'in Bearbeitung' setzen, clicke ich auf das Ziel.

<video controls width="240" height="320" >
<source src="images/ZielAnlegen_KinnTarget_edited.mp4">
</video>

### Unterziele definieren

Merke ich, dass das Ziel zu groß ist und zu viele Trainingsschritte beinhaltet, definiere ich mir
kleinere Unterziele. Dazu wische ich das aktuelle Ziel einfach nach links und lege die kleineren
Ziele an. Das wiederhole ich so lange, bis ich das Ziel in trainierbare Schritte zerlegt
habe. Natürlich kann ich auf einem Level mehrere Ziele anlegen.

<video controls width="240" height="320" >
<source src="images/UnterzieleAnlegen_KinnTarget.mp4">
</video>

### Durch Ziele navigieren

Um von einem detaillierten Ziel wieder nach oben zu navigieren, wische ich entweder ein Ziel
nach rechts oder verwende den Backbutton. Hab ich eine Gruppe von Zielen auf dem gleichen
Level, kann ich die Reihenfolge verändern. Damit kann ich mir vorgeben, welches Ziel zuerst
erarbeitet werden soll.

<video controls width="240" height="320" >
<source src="images/Navigation_Kinntarget_small.mp4">
</video>

## Training planen

Zu jedem Ziel kann ich ein Training anlegen. Dazu clicke ich auf den grünen
'Planen' Text des Ziels. Jetzt kann ich festlegen, was und wie ich trainieren will.

### Wie will ich trainieren?

Um Kriterien schnell an das Verhalten des Hundes anzupassen, versuche ich,
meine Trainingseinheiten kurz zu halten. Dazu lege ich vor dem Training
entweder die Zeit oder die Anzahl der gezeigten Verhalten fest, nach 
denen ich stoppen will.
<dl>
<dt>Zeit basiert</dt>
<dd>Ich wähle die Dauer einer Trainingseinheit. Sobald ich mit der Einheit 
beginne, startet PlanTrainDoc einen Timer. Ist die Zeit abgelaufen,
werde ich durch Vibration informiert und das Training gestoppt.</dd>
<dt>Anzahl Verhalten des Tieres</dt>
<dd>Alternativ kann ich die Anzahl der Verhalten des Tieres vorgeben. Z.B. kann
ich das Training nach 5 Verhalten (ob erfolgreich oder nicht) stoppen lassen.</dd>
<dt>Frei</dt>
<dd>Keine Einschränkung</dd>
</dl>

### Was will ich trainieren?

Menschen sind schlecht darin sich zufällig zu verhalten. Das ist ein Problem,
wenn ich ein Kriterium variabel halten will. Daher lasse ich mir die Kriterien
von meiner App vorgeben. PlanTrainDoc verwendet für 
Distanz und Dauer Zahlenreihen, die auf Morgan Spectors Buch 
[Clicker Training for Obedience](https://www.amazon.de/Clicker-Training-Obedience-Shaping-Performance-Positively/dp/0962401781/ref=sr_1_2?__mk_de_DE=%C3%85M%C3%85%C5%BD%C3%95%C3%91&dchild=1&keywords=Clicker+training+for+obedience&qid=1630042590&sr=8-2)
basieren. 
<dl>
<dt>Dauer</dt> 
<dd>Hier lege ich die mittlere Dauer eines Versuches fest. Während des 
Trainings gibt mir PlanTrainDoc dann unterschiedliche Zeiten um diesen
Mittelwert vor. Von 0,1 bis 1,5 Sekunden wird eine konstante Zeit vorgegeben,
danach wird sie variiert. Ich starte jeden Versuch in der App. Ist die vorgegebene Zeit
abgelaufen, werde ich durch Vibration informiert. Ich kann zu jedem Zeitpunkt den Versuch abbrechen
oder clicken.
<a href="https://joerg-schultz.github.io/PlanTrainDoc/durationscheme.txt">Werte</a></dd>
<dt>Distanz</dt>
<dd>Hier lege ich als Kriterium eine mittlere Distanz fest. Wie bei der Dauer
gibt mir PlanTrainDoc während des Trainings eine Distanz vor, die sich variabel um
diesen Mittelwert bewegt. <a href="https://joerg-schultz.github.io/PlanTrainDoc/durationscheme.txt">Werte</a></dd>
<dt>Signal unterscheiden</dt>
<dd>Bei der Signalunterscheidung (z.B. Platz vs. Sitz aus dem Stehen) soll
der Hund nur das Signal als Unterscheidungskriterium haben und nicht eine
Reihenfolge der Signale. In PlanTrainDoc kann ich eine beliebige Anzahl von
Signalen vorgeben. Während des Trainings wird dann gleichverteilt aus diesen
Signalen gezogen.</dd>
<dt>Signal einführen</dt>
<dd>Unter Signalkontrolle soll der Hund das Verhalten erst dann zeigen, wenn 
das Signal gegeben wird. Eine Möglichkeit, dies zu trainieren, ist, den Hund auch
für das Warteverhalten zu belohnen. Mit PlanTrainDoc kann ich mir vorgeben
lassen, wie oft ich das Warteverhalten belohne bzw. das Signal gebe.</dd>
<dt>Frei</dt>
<dd>Keine Hilfe</dd>
</dl>

<video controls width="240" height="320" >
<source src="images/Planen_Kinntarget_small.mp4">
</video>

## Trainieren und Dokumentieren

Um das Training zu starten, clicke ich auf den grünen 'Training' Text
eines Ziels. Im Training verwende ich PlanTrainDoc als Clicker. Da ich
zusätzlich auch jeden "Fehler“ (neutraler: Reset) notiere, habe ich jederzeit
während des Trainings die genaue Erfolgsrate.

Je nach gewählter Trainingsart gibt mir PlanTrainDoc hier Kriterien vor. In den
Einstellungen (Zahnrad oben rechts) kann ich wählen, ob mir das Kriterium angesagt oder
nur angezeigt wird.

Vor jeder Trainingseinheit schreibe ich das aktuelle Kriterium auf,
nach der Einheit dokumentiere ich direkt den Erfolg der Einheit. Dadurch erstelle
ich während des Trainings direkt meine Dokumentation. 

<video controls width="240" height="320" >
<source src="images/Training_Kinntarget_edited.mp4">
</video>

## Auswerten

PlanTrainDoc merkt sich jedes erfolgreiche (Click) und nicht erfolgreiche (Reset)
Verhalten. Damit kann ich den Erfolg jedes Trainings, jedes
Unterziels und jedes Ziels nachvollziehen. Dazu clicke ich auf den 'Auswerten' Text des Ziels.
Hier bekomme ich die Ergebnisse des Trainings für dieses Ziel und aller seiner
Unterziele angezeigt. Ich kann mir zum einen Überblick über die erfolgreichen (geclickten)
und die nicht geclickten Versuche anzeigen lassen. Zum anderen kann ich mir den zeitlichen
Verlauf meines Trainings graphisch darstellen lassen. Hier sehe ich, ob mein Hund häufig
gecklickte (ansteigende Linie) oder nicht gecklickte (horizontale Linie) Verhalten gezeigt hat.
Clicke ich auf einen Punkt, wird mir angezeigt, zu welchem Ziel dieses Verhalten gehört hat.

<video controls width="240" height="320" >
<source src="images/Auswertung_Kinntarget_small.mp4">
</video>