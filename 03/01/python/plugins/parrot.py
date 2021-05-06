class Parrot:
  def __init__(self, name):
    self.name = name
  def name(self):
    return self.name
  def greeting(self):
    return "parrot greeting"
  def menu(self):
    return "parrot menu"


def create(name):
  return Parrot(name)