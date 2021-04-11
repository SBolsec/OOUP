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
  strings = [
    "Gle", "malu", "vocku", "poslije", "kise",
    "Puna", "je", "kapi", "pa", "ih", "njise"
  ]

  longest_string = mymax(strings, lambda x: len(x))
  print(longest_string)

  maxint = mymax([1, 3, 5, 7, 4, 6, 9, 2, 0])
  maxchar = mymax("Suncana strana ulice")
  maxstring = mymax(strings)
  print(maxint)
  print(maxchar)
  print(maxstring)

  D={'burek':8, 'buhtla':5}
  print(mymax(D, key=D.get))
  # Metodu D.get možemo koristiti kao slobodnu funkciju zato što su u pythonu funkcije
  # ravnopravne objektima. Metoda D.get sadrži referencu na rječnik D te kada će se 
  # pozvati biti će moguće preko te reference dohvatiti ispravnu vrijednost iz riječnika

  persons = [("pero", "peric"), ("juro", "juric"), ("pero", "horvat"), ("marko", "markovic")]
  print(mymax(persons))