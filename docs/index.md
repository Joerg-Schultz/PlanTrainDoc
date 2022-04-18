---
layout: default
---
<div style="float:right"><b><a href="index_de">DE</a> | EN</b></div>

# Why might this App help you (executive summary)

[Ziele festlegen](#Ziele-festlegen)

[Training planen](#Training-planen)

[Trainieren und Dokumentieren](#Trainieren-und-Dokumentieren)

[Auswerten](#Auswerten)

# Why this App?

When training my dog, I repeatedly ran into the same challenges:

- I just can't mentally keep track of successful and failed trials while at the same time focussing on the actual training.
- I am a big fan of timed training sessions. Still, after a few sessions, I tend to forget to start the timer. And it gets even worse when limiting the sessions by the number of repetitions. Was it the fourth or already the fifth trial?
- When training with variable criteria, I had to write those down before the training. While training, I had to check my notes and lost focus of the dog.
- We all are well aware of the importance of data and documentation. But, documenting on paper while training broke my training flow.
- And it gets worse when documenting with pen and paper when training outside. And its windy. Or raining...
- Every plan has to be adopted to the real behavior. Using plans on paper, I had to stop the training (which can be a good thing!) even for the slightest modifications.

And that was the point where I realised it time to change my behaviour. So I developed an App to address these challenges: [PlanTrainDoc](https://play.google.com/store/apps/details?id=de.tierwohlteam.android.plantraindoc). By now, I use this App for all steps of my training - developing a training plan, performing the training with automated documentation, data driven evaluation and adaption of the plan. In the training phase, the App tells me when to stop a session based on time or number of trials. Additionally, when working with variable criteria, the app tells me the actual value for a trial. And as everything is on my Smartphone, I have all the data in my pocket.

---

# Documentation

## Developing a training plan

### Setting a new goal

As first step, Yoi can add a high level goal. Just click on the orange '+' button. Now you can set a title and describe the behaviour in detail. Additionally, you can set the status of the goal. Store the goal by clicking on the orange 'disk' button. Of course, you can change all the details of the goal later on by just clicking on the goal.

<video controls width="240" height="320" >
<source src="images/ZielAnlegen_KinnTarget_edited.mp4">
</video>

### Split it into smaller goals

As next step, you can break your goal down into smaller training steps. Just swipe the goal you want to break down to the left. You are now a level and can add your sub goals (as many as you want) as before. Of course, you can repeat this step for each of the sub goals until you broke down your plan into trainable chunks. There is no limit on the levels, just split your goals as small as necessary.

<video controls width="240" height="320" >
<source src="images/UnterzieleAnlegen_KinnTarget.mp4">
</video>

### Navigate through your goals

After the brainstorming phase, where you broke down your goals into small, trainable steps, you can start to sort your goals. Within one level, you can just drag a goal up and down until they are in the order you want to train. And of course you can move a level up by either swiping a goal to the left or using the back button.

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
<dd>Hier lege ich die mittlere Dauer eines Versuches in Sekunden fest. Während des 
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

--- 

<video controls width="240" height="320" >
<source src="images/Planen_Kinntarget_small.mp4">
</video>

## Trainieren und Dokumentieren

Um das Training zu starten, clicke ich auf den grünen 'Training' Text
eines Ziels. Im Training verwende ich PlanTrainDoc als Clicker. Da ich
zusätzlich auch jeden "Fehler“ (neutraler: Reset) notiere, habe ich jederzeit
während des Trainings die genaue Erfolgsrate. Über die Einstellungen kann
ich das Clickgeräusch auch ausschalten.

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
