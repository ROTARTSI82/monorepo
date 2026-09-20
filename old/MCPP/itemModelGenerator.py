try:
    item = raw_input("item name> ")
except:
    item = input("item name> ")

with open("src/main/resources/assets/mcpp/models/item/%s.json" % item, "w") as fp:
    fp.write("""
{
  "parent": "item/generated",
      "textures": {
          "layer0": "mcpp:items/%s"
  }
}""" % item)

with open("src/main/resources/assets/mcpp/textures/items/%s.png" % item, "w") as fp:
    pass


print ("Success!")
