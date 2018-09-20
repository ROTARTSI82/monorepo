#!venv/bin python
# -*- coding: UTF-8 -*-

"""
(.../TCBS/data/resources/scripts/mainloop.py)

"""
import webbrowser

if False:
    # Ignore this code. It makes PyCharm happy
    # Since I call this script via execfile, PyCharm thinks
    # all the variables are undefined and gives me endless warnings :(
    from load import *
    from multiplayer import *
    from funcsAndClasses import *
    from CONFIG import *
    from __main__ import *

red, green, blue = sky_blue
updateoptions()
updaterects()

while running:
    cursor.rect.center = pygame.mouse.get_pos()
    cursor.rect.center = (cursor.rect.center[0] * resWinRate[0], cursor.rect.center[1] * resWinRate[1])
    clock.tick(desiredFPS)
    cbCollide = pygame.sprite.spritecollide(cursor, buttons, False)
    buttons.update(cbCollide)
    screen.fill([red, green, blue])
    if __debugMode__:
        DebugFPSText = simpleFont.render("FPS: %.3f" % clock.get_fps(),
                                         False, [0, 0, 0], [255, 255, 255])
        screen.blit(DebugFPSText, [10, 10])
        screen.blit(cursor.image, cursor.rect)

    if state == "menu":
        if badVerDetect:
            screen.blit(badVerWarn.image, badVerWarn.rect)
        if newUpDetect:
            screen.blit(newUpNote.image, newUpNote.rect)
        screen.blit(playBt.image, playBt.rect)
        screen.blit(mltPlayBt.image, mltPlayBt.rect)
        screen.blit(optionsBt.image, optionsBt.rect)
        screen.blit(tcbs_title.image, tcbs_title.rect)
        screen.blit(tcbs_subtitle.image, tcbs_subtitle.rect)
        updatesurf()
        for event in pygame.event.get():
            updateevent()
            if event.type == MOUSEBUTTONDOWN:
                if optionsBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    state = "options"
                if mltPlayBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    state = "mult-start"
                if newUpNote in cbCollide and event.button == 1 and newUpDetect:
                    menuBlip.play()
                    try:
                        webbrowser.open("https://rotartsi82.github.io/TCBS/downloads.html")
                    except Exception as e:
                        if not str(e) in alreadyHandled:
                            log("EXCEPTION", "Cannot open link: "+str(e))
                            alreadyHandled.append(str(e))
                if badVerWarn in cbCollide and event.button == 1 and badVerDetect:
                    menuBlip.play()
                    try:
                        webbrowser.open("https://github.com/ROTARTSI82/TCBS/wiki/Python-&-Pygame-Versions")
                    except Exception as e:
                        if not str(e) in alreadyHandled:
                            log("EXCEPTION", "Cannot open link: "+str(e))
                            alreadyHandled.append(str(e))
                if playBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    state = "sndbx-placeUnits"
                    updateselectedunit(0)
                    set_music("resources/sounds/in-gameMusic.mp3")
                    red, green, blue = grass_green
    if state == "options":
        screen.blit(backBt.image, backBt.rect)
        screen.blit(coinRRBt.image, coinRRBt.rect)
        screen.blit(startBdgtBt.image, startBdgtBt.rect)
        screen.blit(musicVolBt.image, musicVolBt.rect)
        screen.blit(effectsVolBt.image, effectsVolBt.rect)
        screen.blit(fpsBt.image, fpsBt.rect)
        screen.blit(fontBt.image, fontBt.rect)
        screen.blit(guiScaleBt.image, guiScaleBt.rect)
        screen.blit(langBt.image, langBt.rect)
        screen.blit(check4updatesBt.image, check4updatesBt.rect)
        screen.blit(onBattleEndBt.image, onBattleEndBt.rect)
        updatesurf()
        for event in pygame.event.get():
            updateevent()
            if event.type == MOUSEBUTTONDOWN:
                if backBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    state = "menu"
                if coinRRBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    options['coinRR'] += coinRRCR
                    updateoptions()
                if coinRRBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    options['coinRR'] -= coinRRCR
                    updateoptions()
                if startBdgtBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    options['srtBdgt'] += startBdgtCR
                    updateoptions()
                if startBdgtBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    options['srtBdgt'] -= startBdgtCR
                    updateoptions()
                if fpsBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    options['fps'] += fpsCR
                    updateoptions()
                if fpsBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    options['fps'] -= fpsCR
                    updateoptions()
                if guiScaleBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    options['scale'] += scaleCR
                    updateoptions()
                if guiScaleBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    options['scale'] -= scaleCR
                    updateoptions()
                if musicVolBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    options['music'] += musicCR
                    updateoptions()
                if musicVolBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    options['music'] -= musicCR
                    updateoptions()
                if effectsVolBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    options['effects'] += effectsCR
                    updateoptions()
                if effectsVolBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    options['effects'] -= effectsCR
                    updateoptions()
                if langBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    langIndex += 1
                    if langIndex > len(langList)-1:
                        langIndex = 0
                    if langIndex < 0:
                        langIndex = len(langList)-1
                    options['lang'] = langList[langIndex]
                    updateoptions()
                if langBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    langIndex -= 1
                    if langIndex > len(langList) - 1:
                        langIndex = 0
                    if langIndex < 0:
                        langIndex = len(langList) - 1
                    options['lang'] = langList[langIndex]
                    updateoptions()
                if fontBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    fontIndex += 1
                    if fontIndex > len(fontList)-1:
                        fontIndex = 0
                    if fontIndex < 0:
                        fontIndex = len(fontList)-1
                    options['font'] = fontList[fontIndex]
                    updateoptions()
                if fontBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    fontIndex -= 1
                    if fontIndex > len(fontList) - 1:
                        fontIndex = 0
                    if fontIndex < 0:
                        fontIndex = len(fontList) - 1
                    options['font'] = fontList[fontIndex]
                    updateoptions()
                if onBattleEndBt in cbCollide and event.button == 3:
                    menuBlip.play()
                    onBattleEndIndex -= 1
                    if onBattleEndIndex > len(onBattleEndList) - 1:
                        onBattleEndIndex = 0
                    if onBattleEndIndex < 0:
                        onBattleEndIndex = len(onBattleEndList) - 1
                    options['battleEnd'] = onBattleEndList[onBattleEndIndex]
                    updateoptions()
                if onBattleEndBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    onBattleEndIndex += 1
                    if onBattleEndIndex > len(onBattleEndList) - 1:
                        onBattleEndIndex = 0
                    if onBattleEndIndex < 0:
                        onBattleEndIndex = len(onBattleEndList) - 1
                    options['battleEnd'] = onBattleEndList[onBattleEndIndex]
                    updateoptions()
                if check4updatesBt in cbCollide and event.button in [1, 3]:
                    menuBlip.play()
                    if check4updates:
                        options['check4updates'] = False
                    elif not check4updates:
                        options['check4updates'] = True
                    updateoptions()
    if state == "sndbx-placeUnits":
        pygame.draw.line(screen, [0, 200, 0], [screen.get_width() / 2, -5],
                         [screen.get_width() / 2, screen.get_height() + 5], 5)
        sndbxRUnits.draw(screen)
        sndbxBUnits.draw(screen)
        screen.blit(startBt.image, startBt.rect)
        screen.blit(backBt.image, backBt.rect)
        screen.blit(selectedUnitTxt.image, selectedUnitTxt.rect)
        screen.blit(redCostTxt.image, redCostTxt.rect)
        screen.blit(blueCostTxt.image, blueCostTxt.rect)
        screen.blit(nextBt.image, nextBt.rect)
        screen.blit(prevBt.image, prevBt.rect)
        screen.blit(redBar.image, redBar.rect)
        screen.blit(blueBar.image, blueBar.rect)
        screen.blit(teamSelectBt.image, teamSelectBt.rect)
        screen.blit(clearBlueBt.image, clearBlueBt.rect)
        screen.blit(clearRedBt.image, clearRedBt.rect)
        updatesurf()
        for event in pygame.event.get():
            updateevent()
            if event.type == MOUSEBUTTONDOWN:
                if teamSelectBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    if selectedTeam == "red":
                        selectedTeam = "blue"
                    else:
                        selectedTeam = "red"
                    teamSelectBt.kill()
                    teamSelectBt = TxtOrBt(["Team: " + selectedTeam.upper(), False, [0, 0, 0],
                                            [255, 255, 0]], [None, 45])
                    updaterects()
                    continue
                if clearRedBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    sndbxRUnits = pygame.sprite.Group()
                    updatecost()
                    continue
                if clearBlueBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    sndbxBUnits = pygame.sprite.Group()
                    updatecost()
                    continue
                if startBt in cbCollide and event.button == 1:
                    bullets = pygame.sprite.Group()
                    menuBlip.play()
                    oldRUnits = []
                    oldBUnits = []
                    for i in sndbxRUnits:
                        oldRUnits.append(type(i)(*i.pack()))
                    for i in sndbxBUnits:
                        oldBUnits.append(type(i)(*i.pack()))
                    state = "sndbx-battle"
                    log("BATTLE", "Battle started")
                    continue
                if backBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    state = "menu"
                    set_music("resources/sounds/menuMusic.mp3")
                    red, green, blue = sky_blue
                    continue
                if nextBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    updateselectedunit(+1)
                    continue
                if prevBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    updateselectedunit(-1)
                    continue

                if event.button == 1:
                    menuBlip.play()
                    try:
                        if selectedTeam == "red":
                            sndbxRUnits.add(sbUnits[sbUnitInt](cursor.rect.center, "red"))
                        else:
                            sndbxBUnits.add(sbUnits[sbUnitInt](cursor.rect.center, "blue"))
                    except Exception as e:
                        if __debugMode__:
                            raise
                        if str(e) not in alreadyHandled:
                            alreadyHandled.append(str(e))
                            log("EXCEPTION", "Place unit failed: "+str(e))
                    updatecost()
                if event.button == 3:
                    menuBlip.play()
                    pygame.sprite.spritecollide(cursor, sndbxBUnits, True)
                    pygame.sprite.spritecollide(cursor, sndbxRUnits, True)
                    updatecost()
            if event.type == KEYDOWN:
                if event.key == toggleTeamKey:
                    menuBlip.play()
                    if selectedTeam == "red":
                        selectedTeam = "blue"
                    else:
                        selectedTeam = "red"
                    teamSelectBt.kill()
                    teamSelectBt = TxtOrBt(["Team: " + selectedTeam.upper(), False, [0, 0, 0],
                                            [255, 255, 0]], [None, 45])
                    updaterects()
                if event.key == prevUnitKey:
                    menuBlip.play()
                    updateselectedunit(-1)
                if event.key == nextUnitKey:
                    menuBlip.play()
                    updateselectedunit(1)
                if event.key == startKey:
                    bullets = pygame.sprite.Group()
                    menuBlip.play()
                    oldRUnits = []
                    oldBUnits = []
                    for i in sndbxRUnits:
                        oldRUnits.append(type(i)(*i.pack()))
                    for i in sndbxBUnits:
                        oldBUnits.append(type(i)(*i.pack()))
                    state = "sndbx-battle"
                    log("BATTLE", "Battle started")
                if event.key == clearBlueKey:
                    menuBlip.play()
                    sndbxBUnits = pygame.sprite.Group()
                    updatecost()
                if event.key == clearRedKey:
                    menuBlip.play()
                    sndbxRUnits = pygame.sprite.Group()
                    updatecost()
    if state == "sndbx-battle":
        if len(sndbxRUnits) == 0 and len(sndbxBUnits) == 0:
            log("BATTLE", "Draw!")
            bullets = pygame.sprite.Group()
            screen.fill([red, green, blue])
            vicMsg = TxtOrBt(["DRAW!", False, [0, 0, 0]],
                             [None, 50])
            vicMsg.rect.center = [screen.get_width() / 2, screen.get_height() / 2]
            screen.blit(vicMsg.image, vicMsg.rect)
            updatesurf()
            state = "sndbx-placeUnits"
            pygame.time.wait(1000)
        if len(sndbxRUnits) == 0 and len(sndbxBUnits) > 0:
            log("BATTLE", "Blue Victory!")
            bullets = pygame.sprite.Group()
            screen.fill([red, green, blue])
            vicMsg = TxtOrBt(["BLUE VICTORY!", False, [0, 0, 0]],
                             [None, 50])
            vicMsg.rect.center = [screen.get_width() / 2, screen.get_height() / 2]
            screen.blit(vicMsg.image, vicMsg.rect)
            updatesurf()
            state = "sndbx-placeUnits"
            pygame.time.wait(1000)
        if len(sndbxBUnits) == 0 and len(sndbxRUnits) > 0:
            log("BATTLE", "Red Victory!")
            bullets = pygame.sprite.Group()
            screen.fill([red, green, blue])
            vicMsg = TxtOrBt(["RED VICTORY!", False, [0, 0, 0]],
                             [None, 50])
            vicMsg.rect.center = [screen.get_width()/2, screen.get_height()/2]
            screen.blit(vicMsg.image, vicMsg.rect)
            updatesurf()
            state = "sndbx-placeUnits"
            pygame.time.wait(1000)
        if state == "sndbx-placeUnits":

            if onBattleEnd == "Go to start":
                sndbxRUnits = pygame.sprite.Group(*oldRUnits)
                sndbxBUnits = pygame.sprite.Group(*oldBUnits)
            if onBattleEnd == "Clear":
                sndbxBUnits = pygame.sprite.Group()
                sndbxRUnits = pygame.sprite.Group()
            updatecost()
        try:
            BbulletCol = pygame.sprite.groupcollide(bullets, sndbxBUnits, False, False)
            RbulletCol = pygame.sprite.groupcollide(bullets, sndbxRUnits, False, False)
            soldierCol = pygame.sprite.groupcollide(sndbxBUnits, sndbxRUnits, False, False)
            totalBlueHP = 0
            totalRedHP = 0
            for i in sndbxBUnits:
                totalBlueHP += i.health
                i.update()
            for i in sndbxRUnits:
                i.update()
                totalRedHP += i.health
            blueBar.update(totalBlueHP, totalBlueHP + totalRedHP)
            redBar.update(totalRedHP, totalBlueHP + totalRedHP)
            bullets.update()
            for i in BbulletCol.keys():
                i.on_bullet_hit(BbulletCol[i])
                for j in BbulletCol[i]:
                    j.on_bullet_hit([i, ])
            for i in RbulletCol.keys():
                i.on_bullet_hit(RbulletCol[i])
                for j in RbulletCol[i]:
                    j.on_bullet_hit([i, ])
            for i in soldierCol.keys():
                i.on_soldier_hit(soldierCol[i])
                for j in soldierCol[i]:
                    j.on_soldier_hit([i, ])
        except Exception as e:
            if __debugMode__:
                raise
            if str(e) not in alreadyHandled:
                alreadyHandled.append(str(e))
                log("EXCEPTION", "Failed to update AI: "+str(e))
        pygame.draw.line(screen, [0, 200, 0], [screen.get_width() / 2, -5],
                         [screen.get_width() / 2, screen.get_height() + 5], 5)
        bullets.draw(screen)
        sndbxRUnits.draw(screen)
        sndbxBUnits.draw(screen)
        screen.blit(nextBt.image, nextBt.rect)
        screen.blit(prevBt.image, prevBt.rect)
        screen.blit(teamSelectBt.image, teamSelectBt.rect)
        screen.blit(redBar.image, redBar.rect)
        screen.blit(blueBar.image, blueBar.rect)
        screen.blit(selectedUnitTxt.image, selectedUnitTxt.rect)
        updatesurf()
        for event in pygame.event.get():
            updateevent()
            if event.type == MOUSEBUTTONDOWN:
                if teamSelectBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    if selectedTeam == "red":
                        selectedTeam = "blue"
                    else:
                        selectedTeam = "red"
                    teamSelectBt.kill()
                    teamSelectBt = TxtOrBt(["Team: " + selectedTeam.upper(), False, [0, 0, 0],
                                            [255, 255, 0]], [None, 45])
                    updaterects()
                    continue
                if nextBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    updateselectedunit(+1)
                    continue
                if prevBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    updateselectedunit(-1)
                    continue

                if event.button == 1:
                    menuBlip.play()
                    try:
                        if selectedTeam == "red":
                            sndbxRUnits.add(sbUnits[sbUnitInt](cursor.rect.center, "red"))
                        else:
                            sndbxBUnits.add(sbUnits[sbUnitInt](cursor.rect.center, "blue"))
                    except Exception as e:
                        if __debugMode__:
                            raise
                        if str(e) not in alreadyHandled:
                            alreadyHandled.append(str(e))
                            log("EXCEPTION", "Place unit failed: "+str(e))
                    updatecost()
                if event.button == 3:
                    menuBlip.play()
                    pygame.sprite.spritecollide(cursor, sndbxBUnits, True)
                    pygame.sprite.spritecollide(cursor, sndbxRUnits, True)
            if event.type == KEYDOWN:
                if event.key == toggleTeamKey:
                    menuBlip.play()
                    if selectedTeam == "red":
                        selectedTeam = "blue"
                    else:
                        selectedTeam = "red"
                    teamSelectBt.kill()
                    teamSelectBt = TxtOrBt(["Team: " + selectedTeam.upper(), False, [0, 0, 0],
                                            [255, 255, 0]], [None, 45])
                    updaterects()
                if event.key == prevUnitKey:
                    menuBlip.play()
                    updateselectedunit(-1)
                if event.key == nextUnitKey:
                    menuBlip.play()
                    updateselectedunit(+1)
                if event.key == endBattleKey:
                    menuBlip.play()
                    log("BATTLE", "Battle was ended via endBattleKey")
                    bullets = pygame.sprite.Group()
                    state = "sndbx-placeUnits"
                    if onBattleEnd == "Go to start":
                        sndbxRUnits = pygame.sprite.Group(*oldRUnits)
                        sndbxBUnits = pygame.sprite.Group(*oldBUnits)
                    if onBattleEnd == "Clear":
                        sndbxBUnits = pygame.sprite.Group()
                        sndbxRUnits = pygame.sprite.Group()
                    updatecost()
    if state == "mult-start":
        screen.blit(backBt.image, backBt.rect)
        screen.blit(joinBt.image, joinBt.rect)
        screen.blit(serverMsg.image, serverMsg.rect)
        screen.blit(createBt.image, createBt.rect)
        screen.blit(serverHelpBt.image, serverHelpBt.rect)
        screen.blit(serverTxt.image, serverTxt.rect)
        updatesurf()
        for event in pygame.event.get():
            updateevent()
            if event.type == KEYDOWN:
                if event.key == copyKey:
                    menuBlip.play()
                    pygame.scrap.put(SCRAP_TEXT, serverStr)
                    serverMsg = TxtOrBt(["Text Copied to Clipboard", False, [0, 0, 0]], [None, 45])
                    serverMsg.rect.center = [screen.get_width()/2, screen.get_height()/2-45]
                    continue
                if event.key == pasteKey:
                    menuBlip.play()
                    serverStr = pygame.scrap.get(SCRAP_TEXT)
                    if serverStr != "":
                        serverTxt = TxtOrBt([serverStr, False, [0, 0, 0]], [None, 45], "ignoreTranslations")
                    else:
                        serverTxt = TxtOrBt(["host:port", False, [128, 128, 128]], [None, 45])
                    serverTxt.rect.center = [screen.get_width() / 2,
                                             screen.get_height() / 2]
                    continue
                if event.key == K_SPACE:
                    menuBlip.play()
                    serverStr += " "
                if pygame.key.name(event.key) in validChars:
                    menuBlip.play()
                    if not shiftDown:
                        serverStr = serverStr + pygame.key.name(event.key)
                    if shiftDown:
                        serverStr = serverStr + shiftChars[
                            validChars.find(pygame.key.name(event.key))]
                if event.key == K_LSHIFT or event.key == K_RSHIFT:
                    shiftDown = True
                if event.key == K_BACKSPACE:
                    menuBlip.play()
                    try:
                        serverStr = serverStr[:-1]
                    except Exception as e:
                        serverStr = ""
                if serverStr == "":
                    serverTxt = TxtOrBt(["host:port", False, [128, 128, 128]], [None, 45])
                else:
                    serverTxt = TxtOrBt([serverStr, False, [0, 0, 0]], [None, 45], "ignoreTranslations")
                serverTxt.rect.center = [screen.get_width()/2,
                                         screen.get_height()/2]
            if event.type == KEYUP:
                if event.key == K_LSHIFT or event.key == K_RSHIFT:
                    shiftDown = False
            if event.type == MOUSEBUTTONDOWN:
                if joinBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    try:
                        args = serverStr.split(":")
                        selfIsHost = False
                        c = TCBSClient(args[0], int(args[1]))
                        state = "mult-wait4players"
                        set_music("resources/sounds/in-gameMusic.mp3")
                        red, green, blue = grass_green
                    except Exception as e:
                        if str(e) not in alreadyHandled:
                            log("EXCEPTION", "Cannot join game: "+str(e))
                            alreadyHandled.append(str(e))
                        serverMsg = TxtOrBt([str(e), False, [255, 0, 0]], [None, 45])
                        serverMsg.rect.center = [screen.get_width()/2,
                                                 screen.get_height()/2-45]
                if createBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    try:
                        args = serverStr.split(":")
                        selfIsHost = True
                        s = TCBSServer(localaddr=(args[0], int(args[1])))
                        c = TCBSClient(args[0], int(args[1]))
                        state = "mult-wait4players"
                        set_music("resources/sounds/in-gameMusic.mp3")
                        red, green, blue = grass_green
                    except Exception as e:
                        if str(e) not in alreadyHandled:
                            log("EXCEPTION", "Cannot create game: "+str(e))
                            alreadyHandled.append(str(e))
                        serverMsg = TxtOrBt([str(e), False, [255, 0, 0]], [None, 45])
                        serverMsg.rect.center = [screen.get_width()/2,
                                                 screen.get_height()/2-45]
                if serverHelpBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    try:
                        webbrowser.open("https://github.com/ROTARTSI82/TCBS/wiki/Multiplayer")
                    except Exception as e:
                        if not str(e) in alreadyHandled:
                            log("EXCEPTION", "Cannot open link: "+str(e))
                            alreadyHandled.append(str(e))
                if backBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    state = "menu"
    if state == "mult-wait4players":
        c.loop()
        if selfIsHost:
            s.loop()
        screen.blit(backBt.image, backBt.rect)
        screen.blit(wait4plyrsTxt.image, wait4plyrsTxt.rect)
        updatesurf()
        for event in pygame.event.get():
            updateevent()
            if event.type == MOUSEBUTTONDOWN:
                if backBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    state = "mult-start"
                    res = origRes
                    screen = pygame.Surface(res)
                    resWinRate = [float(res[0]) / masterSurf.get_width(), float(res[1]) / masterSurf.get_height()]
                    c.Send({"action": "leave"})
                    c.loop()
                    if selfIsHost:
                        s.shutdown()
                    set_music("resources/sounds/menuMusic.mp3")
                    red, green, blue = sky_blue
            if event.type == QUIT:
                c.Send({"action": "leave"})
                c.loop()
                if selfIsHost:
                    s.shutdown()
                running = False
    if state == "mult-placeUnits":
        c.loop()
        if selfIsHost:
            s.loop()
            c.Send({"action": "updatebullets", "sentbyhost": selfIsHost, "bullets": BBullets.sprites()})
            c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multBUnits.sprites()})
        if not selfIsHost:
            c.Send({"action": "updatebullets", "sentbyhost": selfIsHost, "bullets": RBullets.sprites()})
            c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multRUnits.sprites()})
        bullets = pygame.sprite.Group(*(RBullets.sprites() + BBullets.sprites()))
        # The following code results in segfault
        #    c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multBUnits.sprites()})
        #elif not selfIsHost:
        #    c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multRUnits.sprites()})
        pygame.draw.line(screen, [0, 200, 0], [screen.get_width() / 2, -5],
                         [screen.get_width() / 2, screen.get_height() + 5], 5)
        if (not selfIsHost) or __debugMode__:
            screen.blit(redCostTxt.image, redCostTxt.rect)
            screen.blit(clearRedBt.image, clearRedBt.rect)
            multRUnits.draw(screen)
        if selfIsHost or __debugMode__:
            screen.blit(blueCostTxt.image, blueCostTxt.rect)
            screen.blit(clearBlueBt.image, clearBlueBt.rect)
            multBUnits.draw(screen)
        if __debugMode__:
            screen.blit(simpleFont.render("PING: %.3f" % ((c.unitping*1000 + c.bulletping*1000)/2),
                                          False, [0, 0, 0]), [10, 100])

        screen.blit(readyBt.image, readyBt.rect)
        screen.blit(backBt.image, backBt.rect)
        screen.blit(selectedUnitTxt.image, selectedUnitTxt.rect)
        screen.blit(nextBt.image, nextBt.rect)
        screen.blit(prevBt.image, prevBt.rect)
        screen.blit(redBar.image, redBar.rect)
        screen.blit(blueBar.image, blueBar.rect)
        updatesurf()
        for event in pygame.event.get():
            updateevent()
            if event.type == MOUSEBUTTONDOWN:
                # c.Send({"action": "test"})
                if clearRedBt in cbCollide and event.button == 1 and not selfIsHost:
                    menuBlip.play()
                    multRUnits = pygame.sprite.Group()
                    coinsLeft[1] = vStartBdgt
                    updatecost()
                    continue
                if clearBlueBt in cbCollide and event.button == 1 and selfIsHost:
                    menuBlip.play()
                    multBUnits = pygame.sprite.Group()
                    coinsLeft[0] = vStartBdgt
                    updatecost()
                    continue
                if readyBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    readyBt.kill()
                    if readyBt.display[0] == "READY":
                        c.Send({"action": "ready"})
                        readyBt = TxtOrBt(["CANCEL", False, [0, 0, 0],
                                          [255, 255, 0]], [None, 45])
                    elif readyBt.display[0] == "CANCEL":
                        c.Send({"action": "cancelready"})
                        readyBt = TxtOrBt(["READY", False, [0, 0, 0],
                                          [0, 255, 0]], [None, 45])
                    updaterects()
                    log("BATTLE", "Ready sent")
                    continue
                if backBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    state = "mult-start"
                    res = origRes
                    screen = pygame.Surface(res)
                    resWinRate = [float(res[0]) / masterSurf.get_width(), float(res[1]) / masterSurf.get_height()]
                    c.Send({"action": "leave"})
                    c.loop()
                    if selfIsHost:
                        s.shutdown()
                    set_music("resources/sounds/menuMusic.mp3")
                    red, green, blue = sky_blue
                    continue
                if nextBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    updateselectedunit(+1)
                    continue
                if prevBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    updateselectedunit(-1)
                    continue

                if event.button == 1:
                    menuBlip.play()
                    try:
                        if cursor.rect.center[0] > screen.get_width() / 2 and (not selfIsHost):
                            if coinsLeft[1] - mpUnits[mpUnitInt].cost >= 0:
                                newunit = mpUnits[mpUnitInt](cursor.rect.center, "red", nextRID)
                                multRUnits.add(newunit)
                                coinsLeft[1] -= mpUnits[mpUnitInt].cost
                                multRDict[nextRID] = newunit
                                nextRID += 1
                        if cursor.rect.center[0] < screen.get_width() / 2 and selfIsHost:
                            if coinsLeft[0] - mpUnits[mpUnitInt].cost >= 0:
                                newunit = mpUnits[mpUnitInt](cursor.rect.center, "blue", nextBID)
                                multBUnits.add(newunit)
                                coinsLeft[0] -= mpUnits[mpUnitInt].cost
                                multBDict[nextBID] = newunit
                                nextBID += 1
                    except Exception as e:
                        if __debugMode__:
                            raise
                        if str(e) not in alreadyHandled:
                            alreadyHandled.append(str(e))
                            log("EXCEPTION", "Place unit failed: "+str(e))
                    updatecost()
                if event.button == 3:
                    menuBlip.play()
                    bcol = pygame.sprite.spritecollide(cursor, multBUnits, True)
                    rcol = pygame.sprite.spritecollide(cursor, multRUnits, True)
                    for i in bcol:
                        coinsLeft[0] += i.cost
                    for i in rcol:
                        coinsLeft[1] += i.cost
                    updatecost()
            if event.type == KEYDOWN:
                if event.key == prevUnitKey:
                    menuBlip.play()
                    updateselectedunit(-1)
                if event.key == nextUnitKey:
                    menuBlip.play()
                    updateselectedunit(1)
                if event.key == startKey:
                    menuBlip.play()
                    readyBt.kill()
                    if readyBt.display[0] == "READY":
                        c.Send({"action": "ready"})
                        readyBt = TxtOrBt(["CANCEL", False, [0, 0, 0],
                                           [255, 255, 0]], [None, 45])
                    elif readyBt.display[0] == "CANCEL":
                        c.Send({"action": "cancelready"})
                        readyBt = TxtOrBt(["READY", False, [0, 0, 0],
                                           [0, 255, 0]], [None, 45])
                    updaterects()
                    log("BATTLE", "Ready sent")
                if event.key == clearBlueKey and selfIsHost:
                    menuBlip.play()
                    multBUnits = pygame.sprite.Group()
                    coinsLeft[0] = vStartBdgt
                    updatecost()
                if event.key == clearRedKey and not selfIsHost:
                    menuBlip.play()
                    multRUnits = pygame.sprite.Group()
                    coinsLeft[1] = vStartBdgt
                    updatecost()
            if event.type == QUIT:
                running = False
                c.Send({"action": "leave"})
                c.loop()
                if selfIsHost:
                    s.shutdown()
    if state == "mult-battle":
        c.loop()
        if selfIsHost:
            s.loop()
            c.Send({"action": "updatebullets", "sentbyhost": selfIsHost, "bullets": BBullets.sprites()})
            c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multBUnits.sprites()})
        if not selfIsHost:
            c.Send({"action": "updatebullets", "sentbyhost": selfIsHost, "bullets": RBullets.sprites()})
            c.Send({"action": "updateunits", "sentbyhost": selfIsHost, "units": multRUnits.sprites()})
        bullets = pygame.sprite.Group(*(RBullets.sprites() + BBullets.sprites()))
        if len(multRUnits) == 0 and len(multBUnits) == 0:
            c.Send({"action": "battleover", "result": "draw"})
        if len(multRUnits) == 0 and len(multBUnits) > 0:
            c.Send({"action": "battleover", "result": "blue_victory"})
        if len(multBUnits) == 0 and len(multRUnits) > 0:
            c.Send({"action": "battleover", "result": "red_victory"})

        try:
            BbulletCol = pygame.sprite.groupcollide(bullets, multBUnits, False, False)
            RbulletCol = pygame.sprite.groupcollide(bullets, multRUnits, False, False)
            soldierCol = pygame.sprite.groupcollide(multBUnits, multRUnits, False, False)
            totalBlueHP = 0
            totalRedHP = 0
            for i in multBUnits:
                totalBlueHP += i.health
                i.update(selfIsHost)
            for i in multRUnits:
                totalRedHP += i.health
                i.update(selfIsHost)
            blueBar.update(totalBlueHP, totalBlueHP + totalRedHP)
            redBar.update(totalRedHP, totalBlueHP + totalRedHP)
            BBullets.update(selfIsHost)
            RBullets.update(selfIsHost)
            for i in BbulletCol.keys():
                i.on_bullet_hit(BbulletCol[i], selfIsHost)  # i is a bullet
                for j in BbulletCol[i]:
                    j.on_bullet_hit([i, ], selfIsHost)  # j is a blue soldier
            for i in RbulletCol.keys():
                i.on_bullet_hit(RbulletCol[i], selfIsHost)  # i is a bullet
                for j in RbulletCol[i]:
                    j.on_bullet_hit([i, ], selfIsHost)  # j is a red soldier
            for i in soldierCol.keys():
                i.on_soldier_hit(soldierCol[i], selfIsHost)  # i is a blue soldier
                for j in soldierCol[i]:
                    j.on_soldier_hit([i, ], selfIsHost)  # j is a red soldier
        except Exception as e:
            if __debugMode__:
                raise
            if str(e) not in alreadyHandled:
                alreadyHandled.append(str(e))
                log("EXCEPTION", "Failed to update AI: "+str(e))
        pygame.draw.line(screen, [0, 200, 0], [screen.get_width() / 2, -5],
                         [screen.get_width() / 2, screen.get_height() + 5], 5)
        bullets.draw(screen)
        multRUnits.draw(screen)
        multBUnits.draw(screen)
        if __debugMode__:
            screen.blit(simpleFont.render("PING: %.3f" % ((c.unitping*1000 + c.bulletping*1000)/2),
                                          False, [0, 0, 0]), [10, 100])
        screen.blit(nextBt.image, nextBt.rect)
        screen.blit(prevBt.image, prevBt.rect)
        screen.blit(redCostTxt.image, redCostTxt.rect)
        screen.blit(blueCostTxt.image, blueCostTxt.rect)
        screen.blit(redBar.image, redBar.rect)
        screen.blit(blueBar.image, blueBar.rect)
        screen.blit(selectedUnitTxt.image, selectedUnitTxt.rect)
        screen.blit(ceasefireBt.image, ceasefireBt.rect)
        updatesurf()
        for event in pygame.event.get():
            updateevent()
            if event.type == USEREVENT+1 and selfIsHost:
                s.sendtoall({"action": "addcoins", "amount": vCoinRR})
            if event.type == QUIT:
                running = False
                c.Send({"action": "leave"})
                c.loop()
                if selfIsHost:
                    s.shutdown()
            if event.type == MOUSEBUTTONDOWN:
                if nextBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    updateselectedunit(+1)
                    continue
                if prevBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    updateselectedunit(-1)
                    continue
                if ceasefireBt in cbCollide and event.button == 1:
                    menuBlip.play()
                    ceasefireBt.kill()
                    if ceasefireBt.display[0] == "CEASEFIRE":
                        c.Send({"action": "ceasefire"})
                        ceasefireBt = TxtOrBt(["CANCEL", False, [0, 0, 0],
                                          [255, 255, 0]], [None, 45])
                    elif ceasefireBt.display[0] == "CANCEL":
                        c.Send({"action": "cancelceasefire"})
                        ceasefireBt = TxtOrBt(["CEASEFIRE", False, [0, 0, 0],
                                          [0, 255, 0]], [None, 45])
                    updaterects()
                    log("BATTLE", "Ceasefire request sent")
                    continue

                if event.button == 1:
                    menuBlip.play()
                    try:
                        if cursor.rect.center[0] > screen.get_width() / 2 and (not selfIsHost):
                            if coinsLeft[1] - mpUnits[mpUnitInt].cost >= 0:
                                newunit = mpUnits[mpUnitInt](cursor.rect.center, "red", nextRID)
                                multRUnits.add(newunit)
                                coinsLeft[1] -= mpUnits[mpUnitInt].cost
                                multRDict[nextRID] = newunit
                                nextRID += 1
                        if cursor.rect.center[0] < screen.get_width() / 2 and selfIsHost:
                            if coinsLeft[0] - mpUnits[mpUnitInt].cost >= 0:
                                newunit = mpUnits[mpUnitInt](cursor.rect.center, "blue", nextBID)
                                multBUnits.add(newunit)
                                coinsLeft[0] -= mpUnits[mpUnitInt].cost
                                multBDict[nextBID] = newunit
                                nextBID += 1
                    except Exception as e:
                        if __debugMode__:
                            raise
                        if str(e) not in alreadyHandled:
                            alreadyHandled.append(str(e))
                            log("EXCEPTION", "Failed to update AI: " + str(e))
                    updatecost()
            if event.type == KEYDOWN:
                if event.key == prevUnitKey:
                    menuBlip.play()
                    updateselectedunit(-1)
                if event.key == nextUnitKey:
                    menuBlip.play()
                    updateselectedunit(1)
                if event.key == startKey:
                    menuBlip.play()
                    ceasefireBt.kill()
                    if ceasefireBt.display[0] == "CEASEFIRE":
                        c.Send({"action": "ceasefire"})
                        ceasefireBt = TxtOrBt(["CANCEL", False, [0, 0, 0],
                                               [255, 255, 0]], [None, 45])
                    elif ceasefireBt.display[0] == "CANCEL":
                        c.Send({"action": "cancelceasefire"})
                        ceasefireBt = TxtOrBt(["CEASEFIRE", False, [0, 0, 0],
                                               [0, 255, 0]], [None, 45])
                    updaterects()
                    log("BATTLE", "Ceasefire request sent")

try:
    connection.Close()
except RuntimeError as e:
    log("LONG-EXCEPTION", "Cannot close connection: "+str(e))

pygame.quit()

end = datetime.datetime.now()
with open("resources/options.pkl", "wb") as fp:
    pickle.dump(options, fp)
log("OPTIONS", "Saved to options.pkl: "+str(options))

log("PERFORMANCE", "FPS: "+str(clock.get_fps()))
log("EXCEPTIONS", "handledExceptions = "+str(alreadyHandled))
log("STOP", "Stopping...")
log("RUNTIME", "Session lasted "+str(end-start))
