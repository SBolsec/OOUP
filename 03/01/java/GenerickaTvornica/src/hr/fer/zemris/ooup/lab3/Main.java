package hr.fer.zemris.ooup.lab3;

import hr.fer.zemris.ooup.lab3.model.Animal;

public class Main {
    public static void main(String[] args) throws Exception {
        Animal[] animals = {
                AnimalFactory.newInstance("Parrot", "Polonije"),
                AnimalFactory.newInstance("Tiger", "Ofelije")
        };

        for (Animal a : animals) {
            a.animalPrintGreeting();
            a.animalPrintMenu();
        }
    }
}
