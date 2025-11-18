| Key          | Value                       |
|--------------|-----------------------------|
| Date :       | 12/18/24                    |
| Time :       | 15:45                       |
| Location :   | DW PC1 Cubicle 13           |
| Chair        | Roy Kester                  |
| Minute Taker | Milosz Dudala               |
| Attendees :  | Derin, Jay, Mihai, Mohammed |
Agenda Items :
- Opening by chair (1 min )
- Check - in : How is everyone doing and how did the midterms go ? (2 min )
- Announcements by the team (1 min )
- Approval of the agenda - Does anyone have any additions ? (1 min )
- Approval of last minutes - Did everyone read the minutes from the previous meeting ? (1 min )
- Announcements by the TA (2 min )
- Presentation of the current app to TA (3 min )
- Talking Points :
- How did the Buddycheck go for everyone - Who did and didn't pass (3 min)
- No meeting for 2 weeks - What has everyone done (4 min)
- How are we going to work over winter break - How to plan around resits ? (4 min)
- URL: stuff not working ? (3 min)
- Server: Still stuff to do for server ? (2 min)
- Client: How far with functionality ? (3 min)
- User experience: Time to shift over to user experience more now ? (3 min)
- New issue creation (2 min)
- Issue distribution (3 min)
- Summarize action points : Who , what , when ? (1 min )
- Feedback round : What went well and what can be improved next time ? (2 min )
- Planned meeting duration != actual duration ? Where / why did you mis - estimate ? (1 min )
- Question round : Does anyone have anything to add before the meeting closes ? (1 min )
- Closure (1 min )

**Meeting notes**

Issues with speaker - 15:46
- sound from the speaker was too quiet, we had to find optimal space for a laptop with a speaker
Opening - 15:48

Check-in - 15:48 
- everyone is doing well, saw last week’s minutes and approved the agenda

Presentation - 15:49 
- we have added a collection dropdown menu, added an interface for editing, adding collections, fixed bugs with null exceptions 

Buddy check - 15:55
- nobody passed the buddy check, usually because of not following the AID model
- we don’t have (or don't know how to) access to actual feedback from colleagues

No meeting for two weeks, who did what in that time - 15:58 
- Roy: fixed window resizing
- Mihai: collection editor window
- Milosz: collection dropdown menu, edited interface for choosing a collection
- Amin: third feature, created endpoints for creating and deleting files
- Derin: test for JSON utility classes
- Jay: reworked search bar, added endpoints for the third feature

How to work during winter break - 16:01 
- some people have resits, they'll find time to work on projects during break
- we should have a meeting on Monday after the break

URL bug - 16:03 
- there was some unnecessary use of @FXML which was commented

Things to do for the server - 16:04
- 4 of us still need contributions for the server, we will split the work wisely so everyone will have enough server contribution
- we still need to test the service for the server

Client - 16:06 
- Amin added client-side issues for the third feature
- we should focus on making UI better, and easier for the user 
- look for any potential reasons for the app to crash, create null checks, etc
- show error messages when a user does something wrong

New issues - 16:13
- server stuff for Milosz, Jay, Roy, Derin
    - Roy - NoteService 
    - Milosz - NoteCollection 
    - Derin - File storage 
    - Jay - File Database 
- Amin - server utils for file endpoints
- Mihai - UI improvement

Feedback for code contribution and code reviews - 16:21
- checkstyle should be run locally 
- making issues smaller (1 issue = 4 hours)
- code review should be 
- pull the branch before we merge it 
- look for null pointer exceptions during the review

Feedback round - 16:30
- Good way of conducting a meeting, nice agenda design

Planned and actual meeting duration - 16:31
- Time overall is close, but some issues were discussed for longer

Question round - 16:31
- Are we expected to test the client side? - tests are expected for all 3 parts, we could have classes that do only logic and classes that do UI and test the logic classes

Closure - 16:33



