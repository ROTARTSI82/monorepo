#!venv/bin python
# -*- coding: UTF-8 -*-

"""
(.../TCBS/data/__main__.py)
------------------------------------------------------------------------
TOTALLY CUSTOMIZABLE BATTLE SIMULATOR a21.18.04.14
------------------------------------------------------------------------
By Grant Yang

Totally Customizable Battle Simulator is a multiplayer 
strategy videogame. You can design and program your 
own soldiers and make them fight against your
friend's soldiers. It is inspired by Totally Accurate
Battle Simulator by Landfall and uses Pygame 1.9 and
Python 2.7. TCBS uses PodSixNet written by chr15m (Chris McCormick).

SEE README.md FOR MORE DETAILS
"""

import os
import sys

__appName__ = "Totally Customizable Battle Simulator"
__version__ = "beta-1.0.0"
__author__ = "Grant Yang <rotartsi0482@gmail.com>"


def executefile(filepath, globalsdict=globals(), localsdict=locals()):
    """
    Since Python3 doesn't have execfile, we use this instead

    :type filepath: str
    :type globalsdict: dict
    :type localsdict: dict
    :rtype: code
    """
    with open(filepath, "r") as fp:
        code = compile(fp.read(), os.getcwd()+"/"+filepath, 'exec')
        exec (code, globalsdict, localsdict)
    return code


def main():
    """
    Run TCBS using the virtual environment

    :return: None
    """
    try:
        executefile("venv/bin/activate_this.py", dict(__file__=os.getcwd() + "venv/bin/activate_this.py"))
    except:
        print ("WARNING: venv is disabled! Assuming that PodSixNet and pygame are installed and up-to-date")
        print ("TCBS works with pygame 1.9.x, PodSixNet 0.9.4, and python 2.7.x")

    sys.path.append(os.getcwd() + "/venv/lib/python2.7/site-packages")

    executefile("CONFIG.py")
    executefile("resources/scripts/load.py")
    executefile("resources/scripts/mainloop.py")


if __name__ == "__main__":
    main()
