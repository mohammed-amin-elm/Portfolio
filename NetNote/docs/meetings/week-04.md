| Key           | Value                     |  
|---------------|---------------------------|  
| Date :        | 4-12-2024                 |  
| Time :        | 13:45-16:30               |  
| Location :    | DW PC1 Cubicle 13         |  
| Chair         | Jay Vrieze                |  
| Minute  Taker | Mohammed Amin El Moussati |  
| Attendees :   | Derin, Miłosz, Roy, Mihai |  

Agenda  Items (45 min):
-  Opening  by  chair  (1 min)
-  Check - in :  How  is  everyone  doing ?  (1 min)
-  Announcements by the team  (2 min)
-  Approval  of  the  agenda  -  Does  anyone  have  any  additions ?  (2 min)
-  Approval  of  last  minutes  -  Did  everyone  read  the  minutes  from  the  previous  meeting ?  (2 min)
-  Announcements  by  the  TA  (2 min)
-  Presentation  of  the  current  app  to  TA  (3 min)
-  Talking  Points :  (around 22 min)
    -  Monday meeting: What we decided - *Inform*  (3 min)
    -  Bugs: Did everyone play around a bit with the current application and find any bugs? - *Discuss* (5  min)
    -  Client: Are we satisfied with the current UI? Do we wanna make any differences? - *Decision making* (4 min)
    -  Server: Discuss what still needs to be added and which methods need to be tested - *Discussion, Brainstorm* (4 min)
    -  Discuss the issues: Divide the issues equally and maybe think about new ones - *Decision making, Discussion* (4 min)
    -  Buddycheck assignment: Remind everyone and review the assignment - *Review* (2 min)
-  Summarize  action  points :  Who ,  what ,  when ?  (2 min)
-  Feedback  round :  What  went  well  and  what  can  be  improved  next  time ?  (3 min)
-  Planned  meeting  duration  !=  actual  duration ?  Where / why  did  you  mis - estimate?  (2  min )
-  Question  round :  Does  anyone  have  anything  to  add  before  the  meeting  closes ?  (2  min )
-  Closure  (1  min)

### Meeting Notes

* Opening (15:47) 
	- Everyone passed the weekly contribution.
* TA annoucements (15:48)
	- Feedback from the TA on the Buddycheck will be checked later, because of the midterm.	
	- All feedback from the TA is formative, and is only there to help the process.
*  Annoucements from the team (15:49)
	- The team passed the Code of Conduct
* Agenda approval (15:50)
	- There was no feedback on the previous minutes. Everyone approves.
* Presenting the current application to the TA (15:52)
	* Mihai presented the current application to the TA.
		* The application contains a graphical user interface, with components like the sidebar, searchbar, markdown rendering, etc.
		* The application has REST endpoint to create, update and delete notes. Note collection can also be created.
* Monday meeting (15:52)
	* The team agreed to not make any huge additions to the project, and instead focus on more trivial tasks like unit tesing. This is because of the midterms next week.
	* Some issues were created for this week.
* Bugs in the current application (15:53)
	- The application throws an exception, if there are no `NoteCollection` entries in the database, because of a `NullPointerException`.
	- The delay when switching notes is to big (currently 5s) and should be changed to around 0.5s.
* Frontend (15:57)
	- The team agreed to improve the design and style of the UI at the last weeks.
	- The team also agreed to implement to language feature at the last weeks.
* Backend (16:01)
	- Work in the ServerUtils class does not count as server-side contribution.
	- In the feature the work should be split, so that everyone has at least 100 lines of code in both the frontend and backend,
* Splitting issues (16:02)
	- Null checks should be added, to prevent unexcepted exceptions.
	- An issue should be added to allow the user to choose the collections
	- The team agreed that a new collection should contain a default note.
	- An issue should be added for the keyboard shortcuts.
	- An issue should be added for the help menu.
* Buddycheck (16:18)
	- The team agreed to be give honest and constructive feedback.
	- The AID model should be used when giving feedback.
* Summary (16:20)
	- Mohammed Amin is going to create the issues after the meeting.
* Feedback round (16:22)
	* The agenda created by the chair was excellent.
	* The silence during the meeting should be prevended in future meetings
* Questions (16:23)
	- Question: Is it required to participate in the weekly contribution, even the team is finished.
	- Answer: Participation in the weekly contribution is required, even if the team is finished early.
* Closure (16:26)