#!/bin/bash
ant compile
cd build
xterm -e java run.Server ../server1.cfg &
xterm -e java run.Server ../server2.cfg &
xterm -e java run.Server ../server3.cfg &
xterm -e java run.Client ../client.cfg &
xterm -e java run.Client ../client2.cfg &

