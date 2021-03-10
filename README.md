# eleven-print-daemon 

## Summary
The print daemon is used to print labels when scanning bikes with bikescanner. It needs to run on any machine where the label printer is attached. Once done any
bikescanner user can print the label to the printer. All the required info and the latest executable is on the Company Shared Google drive. Please go there
for more information. 

## Development Notes
A remote repository is set up on the Company shared Google drive. The build is done there and the executable(jar) is copied to a directory where a user can set up the daemon as a windows service. There is also a directory where the user can set up the daemon using a BAT file. Having some trouble with broadcast when using it. It seems to hang so there is an option to run with a simple select every 10 seconds ( use.broadcast=false ). 

The daemon can be run in debug or prod mode. In debug mode it is not attached to a printer. It will only print out jobs to the screen. It also takes the print_host, the printer_id, allowed_inactivity and use.broadcast as arguments. Allowed inactivity just tells it how long to wait before a reconnect.

Known issues:
 * If the reconnect is done while a user is scanning it may print out multiple labels.
 * On Windows the broadcast or maybe the entire process seems to hang when run on a command window.
 * In 2. I did see that if you press return in the window it will wake up.

## Reference
https://github.com/winsw/winsw/blob/v3/docs/xml-config-file.md 

https://golb.hplar.ch/2020/05/windows-service.html

