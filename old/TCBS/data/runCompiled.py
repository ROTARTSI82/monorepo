# -*- coding: UTF-8 -*-

"""
Run .pyc files
"""

import py_compile
import os
import sys
import marshal


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
    if os.path.exists(filepath):
        py_compile.compile(filepath)

    # Header size changed in 3.3. It might change again, but as of this writing, it hasn't.
    header_size = 12 if sys.version_info >= (3, 3) else 8

    with open(filepath+'c', "rb") as f:
        magic_and_timestamp = f.read(header_size)  # first 8 or 12 bytes are metadata
        code = marshal.load(f)  # rest is a marshalled code object

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
