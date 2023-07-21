package fr.antyss77.dayz;

import fr.antyss77.dayz.item.equipment.armor.Diamond;
import fr.antyss77.dayz.item.equipment.utilities.Parachute;
import fr.antyss77.dayz.item.gun.assaultrifle.AK47;
import fr.antyss77.dayz.item.knive.knife.Opinel;
import fr.antyss77.dayz.item.projectile.grenade.Smoke;

public class Main {
        public static void main(String[] args) {

            AK47 ak47 = new AK47();
            ak47.loadItem();
            System.out.println("---------------");

            Diamond diamond = new Diamond();
            diamond.loadItem();
            System.out.println("---------------");

            Parachute parachute = new Parachute();
            parachute.loadItem();
            System.out.println("---------------");

            Opinel opinel = new Opinel();
            opinel.loadItem();
            System.out.println("---------------");

            Smoke smoke = new Smoke();
            smoke.loadItem();




        }
    }
