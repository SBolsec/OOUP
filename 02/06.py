import re
import ast


class Cell:
  def __init__(self, sheet, exp="0"):
    self.sheet = sheet
    self.observers = []
    self.value = None
    self.exp = exp
    self.set_exp(exp)

  def set_exp(self, exp):
    # check for circular references and store old refs
    self.__check_circular_reference(exp)
    old_refs = set(self.sheet.getrefs(self))
    
    # set values
    self.exp = exp
    self.value = None
    self.value = self.sheet.evaluate(self)

    # dettach from old refs that are not used anymore and attach
    # to new refs 
    new_refs = set(self.sheet.getrefs(self))
    for cell in old_refs - new_refs:
      cell.dettach(self)
    for cell in new_refs - old_refs:
      cell.attach(self)
    self.notify()

  def attach(self, observer):
    self.observers.append(observer)

  def dettach(self, observer):
    self.observers.remove(observer)

  def notify(self):
    for o in self.observers:
      o.update()

  def update(self):
    self.value = self.sheet.evaluate(self)

  def __check_circular_reference(self, exp):
    refs = []
    visited = set()
    for i in re.findall(r"[A-Z]+[0-9]+", exp):
      c = self.sheet.cell(i)
      refs.append(c)
      visited.add(c)
    if self in visited:
      raise RuntimeError("Circular reference!")
    for ref in refs:
      for i in self.sheet.getrefs(ref):
        if i is self:
          raise RuntimeError("Circular reference!")
        if i not in visited:
          refs.append(i)
          visited.add(i)


class Sheet:
  def __init__(self, width, height):
    # create 2D array of cells
    arr = []
    for i in range(0, height):
      x = []
      for j in range(0, width):
        x.append(Cell(sheet=self))
      arr.append(x)

    self.cells = arr
    self.width = width
    self.height = height

  def cell(self, ref):
    index = self.__ref_to_index(ref)
    return self.cells[index[0]][index[1]]

  def set(self, ref, content):
    cell = self.cell(ref)
    cell.set_exp(content)

  def getrefs(self, cell):
    refs = list(map(lambda x: x[1], self.__get_ref_with_cell_name(cell)))
    return refs


  def evaluate(self, cell):
    if (re.search(r"^[0-9]+\.*[0-9]*$", cell.exp)):
      return float(cell.exp)
    try:
      D={}
      for i in self.__get_ref_with_cell_name(cell):
        D[i[0]] = self.evaluate(i[1])
      return eval_expression(cell.exp, D)
    except Exception:
      return None

  def __get_ref_with_cell_name(self, cell):
    refs = []
    for i in re.findall(r"[A-Z]+[0-9]+", cell.exp):
      refs.append((i, self.cell(i)))
    return refs

  def print(self):
    for row in self.cells:
      print(list(map(lambda x: x.value, row)))

  def __ref_to_index(self, ref):
    if not re.match(r"^[A-Z]+[0-9]+$", ref):
      raise RuntimeError("Cell was not named properly! It was {}".format(ref))
    column = re.match(r"[A-Z]+", ref)
    row = re.search(r"[0-9]+", ref)
    column_index = Sheet.__column_name_to_index(column.group())
    row_index = int(row.group()) - 1
    if row_index >= self.height or column_index >= self.width:
      raise IndexError("Index out of bounds! It was {}".format(ref))
    return row_index, column_index

  @staticmethod
  def __column_name_to_index(column):
    if len(column) == 0:
      raise RuntimeError("Column does not exist")
    index = 0
    power = 1
    for i in range(len(column)-1, -1, -1):
      index += (ord(column[i]) - 65 + 1) * power
      power *= 26
    return index - 1


def eval_expression(exp, variables={}):
  def _eval(node):
    if isinstance(node, ast.Num):
      return node.n
    elif isinstance(node, ast.Name):
      return variables[node.id]
    elif isinstance(node, ast.BinOp):
      return _eval(node.left) + _eval(node.right)
    else:
      raise RuntimeError('Unsupported type {}'.format(node))

  node = ast.parse(exp, mode='eval')
  return _eval(node.body)


if __name__=="__main__":
  s=Sheet(5,5)
  print()

  s.set('A1', '2')
  s.set('A2', '5')
  s.set('A3', 'A1+A2')
  s.print()
  print()

  s.set('A1', '4')
  s.set('A4', 'A1+A3')
  s.print()
  print()

  try:
    s.set('A1', 'A3')
  except RuntimeError as e:
    print("Caught exception:", e)
  s.print()
  print()

  s.set('B1', "1")
  s.set("B2", "B1+A1")
  s.set("B3", "B2")
  s.print()
  print()

  try:
    s.set("B1", "B3")
  except RuntimeError as e:
    print("Caught exception:", e)
  s.print()