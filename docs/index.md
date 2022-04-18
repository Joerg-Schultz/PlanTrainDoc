---
layout: default
---

<a href="https://play.google.com/store/apps/details?id=de.tierwohlteam.android.plantraindoc"> <img src="images/play_prism_hlock_2x.png" alt="Google Play"></a>
<div style="float:right"><b><a href="index_de">DE</a> | EN</b></div>

# How can PlanTrainDoc help you?

[Develop a training plan](#developing-a-training-plan)

[Decide how to train a goal](#add-training-details)

[Train and document your training](#train-and-document)

[Evaluate your training success](#evaluate-your-training)

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

As first step, you can add a high level goal. Just click on the orange '+' button. Now you can set a title and describe the behaviour in detail. Additionally, you can set the status of the goal. Store the goal by clicking on the orange 'disk' button. Of course, you can change all the details of the goal later on by just clicking on the goal.

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

## Add training details

### Define your sessions

Once you've split your goal into small trainable units, you can start adding training details to each goal. Therefore, click on the green 'Plan' text of the goal you want to train. Do you want to train in small, defined training sessions? Then you can add either a timer or the number of repetitions you want to train as a limit.

<dl>
<dt>Time based</dt>
<dd>Define the duration of each training session in seconds. The timer is started automatically as soon as you start your training session. After the pre-defined time interval, the Smartphone vibrates and the training is stopped. </dd>
<dt>Repetitions</dt>
<dd>Alternatively, you can provide a number of trials (success and fails). Again, the Smartphone will vibrate and the training is stopped once you reached this number of repetitions.</dd>
<dt>No Constraint</dt>
<dd>Oc course you can also train without any session limits!</dd>
</dl>

### Add Helpers

We humans happen to be pretty bad in generating random numbers - sooner or later we will fall back into a pattern. Which makes the 'variable' criterion predictable for our trainees. To fix this, PlanTrainDoc can support your training by providing (not completely ;-) random criteria for duration, distance and discrimination. 'Not completely random' here means, that the numbers for duration and distance are from Morgan Spectors excellent book [Clicker Training for Obedience](https://www.amazon.de/Clicker-Training-Obedience-Shaping-Performance-Positively/dp/0962401781/ref=sr_1_2?__mk_de_DE=%C3%85M%C3%85%C5%BD%C3%95%C3%91&dchild=1&keywords=Clicker+training+for+obedience&qid=1630042590&sr=8-2). 

<dl>
<dt>Duration</dt> 
<dd>PlanTrainDoc will provide you with a duration for each trial around the value you provide here (in seconds). For 0.1 to 1.5 seconds, the given duration will be constant. For longer durations, it will follow this <a href="https://joerg-schultz.github.io/PlanTrainDoc/durationscheme.txt">duration scheme</a>
Of course you can click or reset a trial at any time, you don't have to wait until the timer has finished.
<dt>Distance</dt>
<dd>PlanTrainDoc will provide you with a variable value for the distance at each trial around the value you choose. Steps and values follow this <a href="https://joerg-schultz.github.io/PlanTrainDoc/durationscheme.txt">distance scheme</a></dd>
<dt>Discrimination</dt>
<dd>When training a discrimination task, it is extremely important that the trainee can not predict the next behaviour. Here, you can provide a list of keywords, from which PlanTrainDoc will draw values uniformly. That is, each of the values has the same probability (think die or coin). </dd>
<dt>Other Criterion</dt>
<dd>Choose this if you don't need any helper for this step.</dd>
</dl>

---

<video controls width="240" height="320" >
<source src="images/Planen_Kinntarget_small.mp4">
</video>

## Train and Document

The strength of PlanTrainDoc is, that you use the same App for planning and training! This allows to automatically document your training and link the data with your goals. To start the training, you just click on the green 'Train' text. On the next view, you will see a list of all your previous training sessions for this goal. This includes the success of each session and optional comments providing you with a fast overview of the training state of this goal.

Before starting a new session, you can note down the next criterion. Then click 'Train' and start your training! There are two large buttons - 'Click' and 'Reset'. If the trainee fulfills your criterion, press 'Click', if not 'Reset'. The App will automatically store each click/reset with the exact time and criterion. Documentation done while training!

If you've chosen a helper for your training, the current criterion will be shown in the lower left. If you selected a limit for your session (time or repetitions), information will be given in the lower right corner. You can stop your training at each time with the back button. If you are training duration (see video below), you start the timer with a click on 'Start Timer'. This will also transform the button into a clicker. Just a single button, no fiddling with a stopwatch and a clicker.

When training a discrimination task, I frequently want to break the pure randomness, e.g. when I realise that one position needs more repetitions. Therefore, in the case of discriminations, you can 'override' PlanTrainDoc and chose a different criterion. Again, this criterion will be stored in PlanTrainDocs database.

Once you've finished your training session (either by pressing the back button or because your time /repetition limit is reached), you can note down a short comment to this session. Thus, you are creating a full documentation with minimal training interruption.

<video controls width="240" height="320" >
<source src="images/Training_Kinntarget_edited.mp4">
</video>

## Evaluate your Training

Having collected all your training data automatically, PlanTrainDoc allows you to evaluate / analyse your training easily. First, you can get some statistics for each training session by clicking on the session. You will get an overview of the successful and failed trials. Furthermore, if PlanTrainDoc provided you with a criterion, you will see a break-down of the success for each criterion. This makes it easy to see whether e.g. there are more fails at larger distances or there is one position in a discrimination which needs more training. And, you can adapt your training immediately - based on real data.

In the goal view, you can click on the green 'Evaluate' text for a goal. This will show you even more data and statistics. First, you will see an evaluation of all training sessions for this goal - again covering the total clicks and resets as well as a break-down according to criteria. In the 'Trend' plot, a click is shown as an increase, a reset as a horizontal line. This plot allows you to easily spot plateaus in your training - and show you, which session / criterion let to this plateau. As one goal can have multiple sub-goals, this view also shows you all the sub-goals, the click vs. reset distribution and the trend. As the trend plot covers all sessions of this goal plus all sub-goals, you get a quick overview of your progress.

Just as a side note: The Trend-Plot is inspired by the figures in B.F. Skinners book 'The behaviour of organisms'

<video controls width="240" height="320" >
<source src="images/PTD_Stats.mp4">
</video>


## Set Preferences

To set some preferences, click on the icon in the top right corner. Here, you can switch off the sound of the clicker. This can be useful, if you want to use PlanTrainDoc for documentation only. In addition, you can also switch off the announcement of criteria in the training. You still get the values displayed, but switching the sound off might reduce distraction for the dog.

Finally, you can connect PlanTrainDoc to additional external tools. At the moment, this covers only the light gate, which I use to make training of cooperation signals for medical training easier. If you're interested, I've put a video about this on [YouTube](https://www.youtube.com/watch?v=R49OgaJhwL0).