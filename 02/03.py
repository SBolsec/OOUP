def mymax(iterable, key=lambda x: x):
  # inicijaliziraj maksimalni element i maksimalni ključ
  max_x=max_key=None
  
  # obiđi sve elemente
  for x in iterable:
    # ako je key(x) najveći -> ažuriraj max_x i max_key
    if max_x is None or key(x) > max_key:
      max_x = x
      max_key = key(x)
  
  # vrati rezultat
  return max_x


if __name__ == "__main__":
  print(mymax([
    "Gle", "malu", "vocku", "poslije", "kise",
    "Puna", "je", "kapi", "pa", "ih", "njise"], lambda x: len(x)))

  maxint = mymax([1, 3, 5, 7, 4, 6, 9, 2, 0])
  maxchar = mymax("Suncana strana ulice")
  maxstring = mymax([
  "Gle", "malu", "vocku", "poslije", "kise",
  "Puna", "je", "kapi", "pa", "ih", "njise"])
  print(maxint)
  print(maxchar)
  print(maxstring)

  D={'burek':8, 'buthla':5}
  print(mymax(D, key=D.get))

  persons = [("pero", "peric"), ("juro", "juric"), ("pero", "horvat"), ("marko", "markovic")]
  print(mymax(persons))