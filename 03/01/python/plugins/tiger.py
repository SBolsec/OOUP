class Tiger:
  def __init__(self, name):
    self.name = name
  def name(self):
    return self.name
  def greeting(self):
    return "tiger greeting"
  def menu(self):
    return "tiger menu"


def create(name):
  return Tiger(name)