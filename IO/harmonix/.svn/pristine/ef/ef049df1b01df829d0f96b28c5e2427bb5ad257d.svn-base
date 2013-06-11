package pl.flexable.harmonix.samples.harmon.util {
  import mx.collections.ArrayCollection;

  import pl.flexable.harmonix.samples.harmon.vo.Curse;
  import pl.flexable.harmonix.samples.harmon.vo.Day;


  /**
   *
   * @author Mateusz
   *
   */
  public class ModelUtil {



    /**
     *
     * @param curses
     *
     */
    public static function fillCurses(curses : ArrayCollection) : void {

      var curseArr : Array = ["Metody rozpoznawania obrazów", "Inzyniera oprogramowania", "Bazy danych",
        "Statystyka", "Matematyka", "Sieciowe systemy multimedialne", "Systemy wysokiej niezawodności",
        "Systemy agnetowe", "Algorytmy i struktury danych", "Podstawy informatyki", "Bazy danych II",
        "Kompilatory", "Metody obliczeniowe", "Systemy gridowe", "Prawo komputerowe", "Języki programowania",
        "Fizyka komputerowa", "Zaawansowane bazy danych", "Grafika komputerowa", "Multimedia",
        "Elektronika", "Systemu operacyjne", "Matematyka dyskretna", "Sieci komputerowe", "Aplikacje WWW",
        "Metody numeryczne", "Paradygmaty programowania", "Język Angielski", "Systemy wbudowane",
        "Projekt zespołowy", "Programowanie niskopoziomowe", "Architektura systemów komputerowych",
        "Wstęp do programowania", "Logika i teoria mnogości", "Środowisko programisty", "Języki, automaty i obliczenia",
        "Podstawy elektrotechniki i elektroniki", "Technika cyfrowa", "Przetwarzanie sygnałów",
        "Techniki transmisji sygnałów", "Laboratorium wirtualne 1", "Sztuczna inteligencja",
        "Zarządzanie zasobami ludzkimi", "Podstawy kompilatorów"];

      for (var i : int = 0; i < curseArr.length; i++) {
        var curse : Curse = new Curse(curseArr[i], getName());
        curses.addItem(curse);
      }
    }


    private static function getName() : String {
      var name : Array = ["Marek", "Jan", "Zdzisław", "Stanisław", "Michał"];
      var surname : Array = ["Kowalski", "Nowak", "Aghowski", "Bilski", "Furman"];
      var indexName : int = randomInRange(0, name.length);
      var indexSurname : int = randomInRange(0, surname.length);

      return "mgr inż. " + name[indexName] + " " + surname[indexSurname];
    }


    /**
     *
     * @param min
     * @param max
     * @return
     *
     */
    private static function randomInRange(min : int, max : int) : int {
      var scale : int = max - min;
      return int(Math.random() * scale + min);
    }


    /**
     *
     * @param days
     *
     */
    public static function fillDays(days : ArrayCollection) : void {
      var day1 : Day = new Day("poniedzialek 20.05");
      var day2 : Day = new Day("wtorek 21.05");
      var day3 : Day = new Day("środa 22.05");
      var day4 : Day = new Day("czwartek 23.05");
      var day5 : Day = new Day("piatęk 24.05");
      var day6 : Day = new Day("sobota 25.05");
      var day7 : Day = new Day("niedziela 26.05");
      var day8 : Day = new Day("poniedziałek 27.05");
      var day9 : Day = new Day("wtorek 28.05");
      var day10 : Day = new Day("sroda 29.05");

      days.addItem(day1);
      days.addItem(day2);
      days.addItem(day3);
      days.addItem(day4);
      days.addItem(day5);
      days.addItem(day6);
      days.addItem(day7);
      days.addItem(day8);
      days.addItem(day9);
      days.addItem(day10);
    }
  }
}