import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

import java.util.Formatter;

public abstract class CoffeeMakerAnimation {

	FIS fis = FIS.load("coffee.fcl", false);
	FuzzyRuleSet fuzzyRuleSet = fis.getFuzzyRuleSet();

	double coffeeAmount = 0;
	double milkAmount = 0;
    double waterAmount = 0;
	int coffeeType;
	int size;

	enum coffee {
		WHITE(1), AMIERICANA(2);

		private coffee(int nr) {
			this.nr = nr;
		}

		public int nr;

	}


	public CoffeeMakerAnimation(coffee coffeeType) {
		this.coffeeType = coffeeType.nr;
	}

	public void newLevel() {
		fuzzyRuleSet.setVariable("coffeeType", coffeeType);
		fuzzyRuleSet.setVariable("size", size);
		fuzzyRuleSet.setVariable("milkAmount", milkAmount);
		fuzzyRuleSet.setVariable("coffeeAmount", coffeeAmount);
		
		fuzzyRuleSet.evaluate();


        double milkChange = fuzzyRuleSet.getVariable("milkAmountOut").getLatestDefuzzifiedValue();
        double coffeeChange = fuzzyRuleSet.getVariable("coffeeAmountOut").getLatestDefuzzifiedValue();
        double waterChange = fuzzyRuleSet.getVariable("waterAmountOut").getLatestDefuzzifiedValue();


        milkAmount += milkChange;
		coffeeAmount += coffeeChange;
        waterAmount += waterChange;

		
		
		double sum = milkAmount + coffeeAmount + waterAmount;
		if (sum > 0.99) {
			milkAmount = milkAmount / sum;
			coffeeAmount = coffeeAmount / sum;
            waterAmount = waterAmount / sum;
		}


        int i = 1;
        for (; i < (coffeeAmount + waterAmount) * 20 + 1; i++) {
            System.out.print("#");
        }

        for (; i < (coffeeAmount + milkAmount + waterAmount) * 20 + 1; i++) {
            System.out.print("%");
        }

        for (; i < 20 + 1; i++) {
            System.out.print(" ");
        }
        System.out.println(
                (new Formatter()).format(
                "| coffee: %f1,4 + %f1.4 milk: %f1.4 + %f1.4 water: %f1.4 + %f1.4",
                        coffeeAmount, coffeeChange,
                        milkAmount, milkChange,
                        waterAmount, waterChange));
    }

	public void startAnimation() {
		//newLevel();
		
		//fuzzyRuleSet.chart();
		
		
		//fuzzyRuleSet.getVariable("coffeeAmountOut").chartDefuzzifier(true);
		
		Thread t = new Thread() {
			
			public void run() {
				while (coffeeAmount + milkAmount + waterAmount < 0.98) {

					newLevel();
                    onTick();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		};
		t.start();
	}

    abstract void onTick();



}
