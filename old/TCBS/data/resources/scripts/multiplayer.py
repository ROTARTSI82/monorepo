#!venv/bin python
# -*- coding: UTF-8 -*-

"""
(.../TCBS/data/resources/scripts/multiplayer.py)

"""
from weakref import WeakKeyDictionary
import time

if False:
    # Ignore this code. It makes PyCharm happy
    # Since I call this script via execfile, PyCharm thinks
    # all the variables are undefined and gives me endless warnings :(
    from load import *
    from mainloop import *
    from funcsAndClasses import *
    from CONFIG import *
    from __main__ import *


class TCBSClient(ConnectionListener):
    """
    ConnectionListener object from PodSixNet.Connection
    """

    def __init__(self, host, port):
        log("CLIENT", "New TCBSClient at %s:%s" % (host, port))
        self.unitping = 0
        self.bulletping = 0
        self.lastunitping = 0
        self.lastbulletping = 0
        self.Connect((host, port))

    def loop(self):
        """
        Is called every tick to update connection

        :rtype: None
        """
        global selfIsHost, c, s, state, serverMsg
        global log, TxtOrBt, set_music, sky_blue
        global screen, connection, red, green, blue
        try:
            connection.Pump()
            self.Pump()
        except Exception as e:
            # if __debugMode__:
            #    raise
            log("EXCEPTION", "Cannot Pump: " + str(e))
            serverMsg = TxtOrBt([str(e), False, [255, 0, 0]], [None, 45])
            serverMsg.rect.center = [screen.get_width() / 2,
                                     screen.get_height() / 2 - 45]
            if str(e) != "Game already full":
                c.Send({"action": "leave"})
                try:
                    connection.Pump()
                    self.Pump()
                except:
                    pass
                if selfIsHost:
                    s.shutdown()
            state = "mult-start"
            set_music("resources/sounds/menuMusic.mp3")
            red, green, blue = sky_blue

    def Network_kick(self, data):
        """
        Raises Exception(data["reason"]) so that we exit after we're kicked

        :param data: {"action": "kick", "reason": "xxx"}
        :rtype: None
        """
        log("CLIENT", "Was kicked because: " + data["reason"])
        raise Exception(data["reason"])

    def Network_addcoins(self, data):
        """
        Add coins every second

        :param data: {"action": "addcoins", "amount": int}
        :rtype: None
        """
        global coinsLeft
        coinsLeft[0] += data["amount"]
        coinsLeft[1] += data["amount"]

    def Network_battleover(self, data):
        """
        Sets state to 'mult-placeUnits' and forwards

        :param data: {"action": "battleover", "result": str}
        :rtype: None
        """
        global state, coinsLeft, multRUnits, multBUnits, oldRUnits, oldBUnits
        global vStartBdgt, RBullets, BBullets, bullets, alreadyHandled, vOnBattleEnd
        global screen, vicMsg, NRBulletID, NBBulletID, RBulletDict, BBulletDict
        if data["result"] == "draw":
            log("CLIENT", "Draw!")
            screen.fill([red, green, blue])
            vicMsg = TxtOrBt(["DRAW!", False, [0, 0, 0]],
                             [None, 50])
            vicMsg.rect.center = [screen.get_width() / 2, screen.get_height() / 2]
            screen.blit(vicMsg.image, vicMsg.rect)
            pygame.display.flip()
            state = "mult-placeUnits"
            updatecost()
            pygame.time.wait(1000)
        if data["result"] == "blue_victory":
            log("CLIENT", "Blue Victory!")
            bullets = pygame.sprite.Group()
            screen.fill([red, green, blue])
            vicMsg = TxtOrBt(["BLUE VICTORY!", False, [0, 0, 0]],
                             [None, 50])
            vicMsg.rect.center = [screen.get_width() / 2, screen.get_height() / 2]
            screen.blit(vicMsg.image, vicMsg.rect)
            pygame.display.flip()
            state = "mult-placeUnits"
            updatecost()
            pygame.time.wait(1000)
        if data["result"] == "red_victory":
            log("CLIENT", "Red Victory!")
            bullets = pygame.sprite.Group()
            screen.fill([red, green, blue])
            vicMsg = TxtOrBt(["RED VICTORY!", False, [0, 0, 0]],
                             [None, 50])
            vicMsg.rect.center = [screen.get_width() / 2, screen.get_height() / 2]
            screen.blit(vicMsg.image, vicMsg.rect)
            pygame.display.flip()
            state = "mult-placeUnits"
            updatecost()
            pygame.time.wait(1000)

        if vOnBattleEnd == "Go to start":
            try:
                multRUnits = pygame.sprite.Group(*oldRUnits)
                multBUnits = pygame.sprite.Group(*oldBUnits)
                coinsLeft = [vStartBdgt, vStartBdgt]
                for i in multRUnits:
                    coinsLeft[1] -= i.cost
                for i in multBUnits:
                    coinsLeft[0] -= i.cost
                updatecost()
            except Exception as e:
                coinsLeft = [0, 0]
                updatecost()
                if __debugMode__:
                    raise
                if str(e) not in alreadyHandled:
                    log("EXCEPTION", "Cannot reset units: "+str(e))
                    alreadyHandled.append(str(e))
            state = "mult-placeUnits"
        if vOnBattleEnd == "Clear":
            multBUnits = pygame.sprite.Group()
            multRUnits = pygame.sprite.Group()
            coinsLeft = [vStartBdgt, vStartBdgt]
            updatecost()
        if vOnBattleEnd == "Do nothing":
            coinsLeft = [vStartBdgt, vStartBdgt]
            updatecost()
        BBullets, RBullets, bullets = (pygame.sprite.Group(),) * 3
        RBulletDict, BBulletDict, NRBUlletID, NBBulletID = ({}, {}, 0, 0)
        state = "mult-placeUnits"

    def Network_updatesets(self, data):
        """
        Make sure the client's settings are the same as the server's

        :param data: {"action": "updatesets", "coinRR": int, "startBdgt": int}
        :rtype: None
        """
        global vCoinRR, vStartBdgt, coinsLeft, multBDict, multRDict, activeRDict
        global multBUnits, multRUnits, nextRID, nextBID, vOnBattleEnd, activeBDict
        log("CHANNEL", "vCoinRR = %s" % str(data['coinRR']))
        log("CHANNEL", "vStartBdgt = %s" % str(data['startBdgt']))
        vCoinRR = data['coinRR']
        vStartBdgt = data['startBdgt']
        vOnBattleEnd = data['battleEnd']
        coinsLeft = [vStartBdgt, vStartBdgt]
        multBDict = {}
        multRDict = {}
        activeRDict = {}
        activeBDict = {}
        nextRID = 0
        nextBID = 0
        multBUnits = pygame.sprite.Group()
        multRUnits = pygame.sprite.Group()
        if selfIsHost:
            c.Send({"action": "updatebullets", "sentbyhost": selfIsHost, "bullets": BBullets.sprites()})
            c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multBUnits.sprites()})
        elif not selfIsHost:
            c.Send({"action": "updatebullets", "sentbyhost": selfIsHost, "bullets": RBullets.sprites()})
            c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multRUnits.sprites()})
        updatecost()

    def Network_battlestart(self, data):
        """
        Start the battle!

        :param data: {"action": "battlestart"}
        :rtype: None
        """
        global state, readyBt, multRUnits, multBUnits, multBDict
        global oldRUnits, oldBUnits, alreadyHandled, multRDict
        global BBullets, RBullets, bullets, nextRID, nextBID
        BBullets, RBullets, bullets = (pygame.sprite.Group(), )*3
        state = "mult-battle"
        readyBt = TxtOrBt(["READY", False, [0, 0, 0], [0, 255, 0]], [None, 45])
        updaterects()
        oldRUnits = []
        oldBUnits = []
        try:
            for i in multRUnits:
                _args = list(i.oldpack())
                _args[2] = nextRID
                _args[3] = 0
                new_unit = type(i)(*_args)
                oldRUnits.append(new_unit)
                multRDict[nextRID] = new_unit
                nextRID += 1
            for i in multBUnits:
                _args = list(i.oldpack())
                _args[2] = nextBID
                _args[3] = 0
                new_unit = type(i)(*_args)
                oldBUnits.append(new_unit)
                multBDict[nextBID] = new_unit
                nextBID += 1
        except Exception as e:
            if __debugMode__:
                raise
            if str(e) not in alreadyHandled:
                log("EXCEPTION", "Failed to pack units: "+str(e))
                alreadyHandled.append(str(e))
        log("CLIENT", "Battle started!")

    def Network_updatebullets(self, data):
        """
        sets BBullets to pygame.sprite.Group(*data["units"]) if data["sentbyhost"]
        sets RBullets to pygame.sprite.Group(*data["units"]) otherwise


        :param data: {"action": "updateunits", "sentbyhost": bool, "bullets": [pygame.sprite.Sprite, ...]}
        :rtype: None
        """
        global BBullets, RBullets, pygame
        global activeBBullets, activeRBullets
        if data["sentbyhost"] and not selfIsHost:
            BBullets = pygame.sprite.Group(*data["bullets"])
            # c.Send({"action": "updatebullets", "sentbyhost": selfIsHost, "bullets": RBullets.sprites()})
            activeBBullets = {}
            for i in data["bullets"]:
                activeBBullets[getattr(i, "bulletid", None)] = i
            if __debugMode__:
                self.bulletping = time.time() - self.lastbulletping
                self.lastbulletping = time.time()
        elif not data["sentbyhost"] and selfIsHost:
            RBullets = pygame.sprite.Group(*data["bullets"])
            # c.Send({"action": "updatebullets", "sentbyhost": selfIsHost, "bullets": BBullets.sprites()})
            activeRBullets = {}
            for i in data["bullets"]:
                activeRBullets[getattr(i, "bulletid", None)] = i
            if __debugMode__:
                self.bulletping = time.time() - self.lastbulletping
                self.lastbulletping = time.time()

    def Network_callfunc(self, data):
        """
        Call multRDict[data['unitid']].data['func'](*data['args'], **data['kwargs']) if data["sentbyhost"]
        and multBDict[data['unitid']].data['func'](*data['args'], **data['kwargs']) otherwise

        :param data: {"action": "callfunc", "func": str, "args": list, "kwargs": dict,
                      "unitid": int, "sentbyhost": bool}
        :rtype: None
        """
        global alreadyHandled, __debugMode__, multRDict, multBDict, selfIsHost
        try:
            if data['sentbyhost'] and (not selfIsHost):
                getattr(multRDict[data['unitid']], data['func'])(*data['args'], **data['kwargs'])
            elif selfIsHost and (not data['sentbyhost']):
                getattr(multBDict[data['unitid']], data['func'])(*data['args'], **data['kwargs'])
        except Exception as e:
            if __debugMode__:
                raise
            if str(e) not in alreadyHandled:
                log("EXCEPTION", "Failed to execute function: "+str(e))
                alreadyHandled.append(str(e))

    def Network_updateunits(self, data):
        """
        sets multBUnits to pygame.sprite.Group(*data["units"]) if data["sentbyhost"]
        sets multRUnits to pygame.sprite.Group(*data["units"]) otherwise


        :param data: {"action": "updateunits", "sentbyhost": bool, "units": [pygame.sprite.Sprite, ...]}
        :rtype: None
        """
        global multRUnits, multBUnits, activeRDict, activeBDict
        if data["sentbyhost"] and not selfIsHost:
            multBUnits = pygame.sprite.Group(*data["units"])
            # c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multRUnits.sprites()})
            updatecost()
            activeBDict = {}
            for i in data["units"]:
                activeBDict[i.unitid] = i
            if __debugMode__:
                self.unitping = time.time() - self.lastunitping
                self.lastunitping = time.time()
        elif not data["sentbyhost"] and selfIsHost:
            multRUnits = pygame.sprite.Group(*data["units"])
            # c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multBUnits.sprites()})
            updatecost()
            activeRDict = {}
            for i in data["units"]:
                activeRDict[i.unitid] = i
            if __debugMode__:
                self.unitping = time.time() - self.lastunitping
                self.lastunitping = time.time()
        #if selfIsHost:
        #    c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multBUnits.sprites()})
        #elif not selfIsHost:
        #    c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multRUnits.sprites()})

    def Network_players(self, data):
        """
        Sets state to 'mult-placeUnits' once two people are conected

        :param data: {"action": "players", "players": len(TCBSServer.players)}
        :rtype: None
        """
        global state, updateselectedunit
        global coinRR, startBdgt, onBattleEnd
        if data["players"] == 2:
            log("CLIENT", "Starting game...")
            state = "mult-placeUnits"
            if selfIsHost:
                c.Send({"action": "updatesets", "coinRR": coinRR,
                        "startBdgt": startBdgt, "battleEnd": onBattleEnd})
            updatecost()
            updateselectedunit(0)

    def Network_test(self, data):  # EXPIREMENTAL
        log("CLIENT", "Got test message: "+str(data))

    def Network_connected(self, data):
        """
        Log that we connected!

        :param data: {"action": "connected"}
        :rtype: None
        """
        log("CLIENT", "Client connected to the server successfully")

    def Network_error(self, data):
        """
        Raises the exception in data["error"] so that we exit

        :param data: {"action": "error", "error": Exception("xxx")}
        :rtype: None
        """
        log("EXCEPTION", "Error: " + str(data['error']))
        self.Send({"action": "leave"})
        raise data['error']

    def Network_disconnected(self, data):
        """
        Raises Exception("Server disconnected") so that we exit

        :param data: {"action": "disconnected"}
        :rtype: None
        """
        log("CLIENT", "Server disconnected")
        self.Send({"action": "leave"})
        raise Exception("Server disconnected")


class TCBSChannel(Channel):
    """
    This is the server representation of a single connected client.
    """

    def __init__(self, *args, **kwargs):
        log("CHANNEL", "New TCBSChannel at " + str(args[1]))
        Channel.__init__(self, *args, **kwargs)

    def Close(self):
        """
        Calls self._server.delplayer(self)
        (Deletes self from self._server.players)

        :rtype: None
        """
        if self in self._server.players:
            log("CHANNEL", "Closing channel...")
            self._server.delplayer(self)
            self._server.Pump()
        else:
            log("CHANNEL", "Cannot close Channel: Channel already closed!")

    def Network_battleover(self, data):
        """
        Sets state to 'mult-placeUnits' and forwards
        message to all connected client(s)

        :param data: {"action": "battleover"}
        :rtype: None
        """
        global state
        self._server.sendtoall(data)
        state = "mult-placeUnits"

    def Network_leave(self, data):
        """
        Calls self._server.shutdown()

        :param data: {"action": "leave"}
        :rtype: None
        """
        log("CHANNEL", "Shutting down server...")
        self._server.shutdown()

    def Network_kick(self, data):
        """
        Raises Exception(data["reason"]) so that we exit after we're kicked

        :param data: {"action": "kick", "reason": "xxx"}
        :rtype: None
        """
        log("CHANNEL", "Was kicked because: " + data["reason"])
        raise Exception(data["reason"])

    def Network_test(self, data):  # EXPIREMENTAL
        log("CHANNEL", "Forwarding test message to all clients: " + str(data))
        self._server.sendtoall(data)

    def Network_callfunc(self, data):
        """
        Forwards message to client(s)

        :param data: {"action": "callfunc", "func": str, "args": list, "kwargs": dict,
                      "unitid": int, "sentbyhost": bool}
        :rtype: None
        """
        self._server.sendtoall(data)

    def Network_updatebullets(self, data):
        """
        Forwards message to client(s)

        :param data: {"action": "updatebullets", "sentbyhost": bool, "bullets": [pygame.sprite.Sprite, ...]}
        :rtype: None
        """
        self._server.sendtoall(data)

    def Network_updateunits(self, data):
        """
        Forwards message to client(s)

        :param data: {"action": "updateunits", "sentbyhost": bool, "units": [pygame.sprite.Sprite, ...]}
        :rtype: None
        """
        #log("CHANNEL", "TCBSChannel.Network_updateunits(%r, %r) has been called" % (self, data))
        self._server.sendtoall(data)

    def Network_updatesets(self, data):
        """
        Make sure the client's settings are the same as the server's

        :param data: {"action": "updatesets", "coinRR": int, "startBdgt": int}
        :rtype: None
        """
        global vCoinRR, vStartBdgt, coinsLeft, multBDict, multRDict, activeRDict
        global multBUnits, multRUnits, nextBID, nextRID, vOnBattleEnd, activeBDict
        log("CHANNEL", "vCoinRR = %s" % str(data['coinRR']))
        log("CHANNEL", "vStartBdgt = %s" % str(data['startBdgt']))
        vCoinRR = data['coinRR']
        vStartBdgt = data['startBdgt']
        vOnBattleEnd = data['battleEnd']
        coinsLeft = [vStartBdgt, vStartBdgt]
        multBDict = {}
        multRDict = {}
        activeRDict = {}
        activeBDict = {}
        nextRID = 0
        nextBID = 0
        multBUnits = pygame.sprite.Group()
        multRUnits = pygame.sprite.Group()
        updatecost()
        #if selfIsHost:
        #    c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multBUnits.sprites()})
        #elif not selfIsHost:
        #    c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multRUnits.sprites()})
        self._server.sendtoall(data)

    def Network_ready(self, data):
        """
        Handle ready requests

        :param data: {"action": "ready"}
        :rtype: None
        """
        global state
        self._server.playersready += 1
        log("CHANNEL", "%s of 2 players ready" % str(self._server.playersready))
        if self._server.playersready > 1:
            state = "mult-battle"
            self._server.sendtoall({"action": "battlestart"})
            self._server.playersready = 0
            log("BATTLE", "Starting battle...")

    def Network_cancelready(self, data):
        """
        Handle cancel ready requests

        :param data: {"action": "cancelready"}
        :rtype: None
        """
        self._server.playersready -= 1
        log("CHANNEL", "%s of 2 players ready" % str(self._server.playersready))

    def Network_players(self, data):
        """
        Sets state to 'mult-placeUnits' once two people are conected

        :param data: {"action": "players", "players": len(self._server.players)}
        :rtype: None
        """
        global state, coinRR, startBdgt, onBattleEnd
        if data["players"] == 2:
            log("CHANNEL", "Starting game...")
            state = "mult-placeUnits"
            if selfIsHost:
                c.Send({"action": "updatesets", "coinRR": coinRR,
                        "startBdgt": startBdgt, "battleEnd": onBattleEnd})
            updatecost()
            updateselectedunit(0)

    def Network_ceasefire(self, data):
        """
        Handle ceasefire requests

        :param data: {"action": "ceasefire"}
        :rtype: None
        """
        global state
        self._server.ceasefirerequests += 1
        log("CHANNEL", "%s of 2 players requesting ceasefire" % str(self._server.ceasefirerequests))
        if self._server.ceasefirerequests > 1:
            state = "mult-placeUnits"
            self._server.sendtoall({"action": "battleover", "result": None})
            self._server.ceasefirerequests = 0
            log("BATTLE", "Ending battle...")

    def Network_cancelceasefire(self, data):
        """
        Handle cancel ceasefire requests

        :param data: {"action": "cancelceasefire"}
        :rtype: None
        """
        self._server.ceasefirerequests -= 1
        log("CHANNEL", "%s of 2 players requesting ceasefire" % str(self._server.ceasefirerequests))


class TCBSServer(Server):
    """
    Server object from PodSixNet.Server
    """
    channelClass = TCBSChannel

    def __init__(self, *args, **kwargs):
        Server.__init__(self, *args, **kwargs)
        log("SERVER", "New TCBSServer at "+str(self.addr))
        self.playersready = 0
        self.ceasefirerequests = 0
        self.players = WeakKeyDictionary()
        log("SERVER", "Waiting for players...")

    def Connected(self, channel, addr):
        """
        Add channel to a WeakKeyDictionary once they're connected

        :type channel: instance
        :type addr: tuple
        :rtype: None
        """
        if len(self.players) < 2:
            self.addplayer(channel)
        else:
            log("SERVER", "Kicked Player%s because Game already full" % str(addr))
            channel.Send({"action": "kick", "reason": "Game already full"})

    def addplayer(self, player):
        """
        Add player to a WeakKeyDictionary
        Kicks new player if two other players are already connected

        :type player: instance
        :rtype: None
        """
        log("SERVER", "New Player" + str(player.addr))
        self.players[player] = True
        self.sendplayers()

    def delplayer(self, player):
        """
        Delete player from WeakKeyDictionary
        Checks if player exists

        :type player: instance
        :rtype: None
        """
        if player in self.players:
            log("SERVER", "Deleting Player" + str(player.addr))
            del self.players[player]
        else:
            log("SERVER", "Cannot delete player: Player doesn't exist!")
        self.sendplayers()

    def sendplayers(self):
        """
        Send some info about self.players to connected clients

        :rtype: None
        """
        log("SERVER", str(len(self.players)) + " player(s) connected")
        self.sendtoall({"action": "players", "players": len(self.players)})

    def sendtoall(self, data):
        """
        Send data to all connected clients

        :param data: {"action": "xxx", ...}
        :rtype: None
        """
        [p.Send(data) for p in self.players]

    def shutdown(self):
        """
        Kicks all clients because "Opponent Disconnected"

        :rtype: None
        """
        log("SERVER", "Shutting down...")
        self.sendtoall({"action": "kick", "reason": "Opponent Disconnected"})
        self.loop()

    def loop(self):
        """
        Is called every tick to update connection

        :rtype: None
        """
        global selfIsHost, c, s, state, serverMsg
        global log, TxtOrBt, set_music, sky_blue
        global screen, red, green, blue
        try:
            self.Pump()
        except Exception as e:
            # if __debugMode__:
            #    raise
            log("EXCEPTION", "Cannot Pump: " + str(e))
            serverMsg = TxtOrBt([str(e), False, [255, 0, 0]], [None, 45])
            serverMsg.rect.center = [screen.get_width() / 2,
                                     screen.get_height() / 2 - 45]
            if str(e) != "Game already full":
                c.Send({"action": "leave"})
                try:
                    self.Pump()
                except:
                    pass
                if selfIsHost:
                    s.shutdown()
            state = "mult-start"
            set_music("resources/sounds/menuMusic.mp3")
            red, green, blue = sky_blue
