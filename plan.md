Team Brogram's Plan - Stackelberg Game
======================================

Document To-do:
---------------
1. Task breakdown chart
2. Freddy to add more detail to 'finding the global maxima'
3. Detail recursive updates
4. Insert linear regression formula
5. Add to future extensions
6. Confirm program overview is correct
7. Delete this section
8. Convert to more appropriate file format

Aims
----
* Maximise profit by choosing best sequence of prices as the leader in a
  Stackelberg game.
* To do this, we must determine the follower's reaction function by analysis of
  historical responses to the leader's prices.

Program overview
----------------
1. On game agent intialization, parse CSV and perform batch regression to find
   follower's reaction function, R(x).
2. For the first day, find global maxima of R(x) to obtain price to submit.
3. On proceeding to a new day, take previous follower's price and perform
   recursive regression to efficiently update approximation of R(x).
3. Again, find maxima of updated R(x) and submit price. Repeat for each new day.

Platform
--------
* Discussed switching to JRuby but decided to stick with Java for
compatability.
* Converted Excel spreadsheet to two CSV files.

Team organisation
-----------------
* Regular weekly meetings
* Converse frequently via a group chat
* Update the wiki to keep track of progress and suggest new ideas
* Freddy, I remember you made a chart for this last time, should we have another
here?

Learning the reaction funciton
------------------------------
* Currently assuming the follower's reaction function is linear, so simply representing the
  function as two variables, a and b, from R(x) = a + bx.
* We then parse CSV data files to obtain historical data on follower responses
* After, we perform linear regression via least-squares on this data to to find values for a and b.
* Regression performed using formula from Xiao-Jun's fourth lecture, slide 20:
* Include formula here?
* Our next task is to find the global maxima of the function.

Finding the global maxima
-------------------------
* Having estimated the follower's reaction function, R(x), we will then
  calculate our optimal strategy by maximising the (leader's) payoff function, *JL[]*.

Recursive least-squares approach
-------------------
* I don't really get this yet

Future extensions
-----------------
* Multi-variate regression for closer approximation of R(x).
* Will require a more-advanced representation of R(x) than currently
  implemented.
* Anything else?
