package net.sothatsit.heads.lang;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.lang.LangMessage;

public class Lang {
    
    public static LangMessage get(String key) {
        return Heads.getLangConfig().getMessage(key);
    }
    
    public static class HelpSection {
        
        private String key;
        
        public HelpSection(String key) {
            this.key = key;
        }
        
        public String key() {
            return key + ".help";
        }
        
        public String command() {
            return get(key() + ".command").getSingle();
        }
        
        public String description() {
            return get(key() + ".description").getSingle();
        }
        
    }
    
    public static class Menu {
        
        public static String key() {
            return "menu";
        }
        
        public static class Get {
            
            public static String key() {
                return Menu.key() + ".get";
            }
            
            public static LangMessage open() {
                return Lang.get(key() + ".open");
            }
            
            public static LangMessage added() {
                return Lang.get(key() + ".added");
            }
            
            public static LangMessage notEnoughMoney() {
                return Lang.get(key() + ".not-enough-money");
            }
            
            public static LangMessage transactionError() {
                return Lang.get(key() + ".transaction-error");
            }
            
            public static LangMessage categoryPermission() {
                return Lang.get(key() + ".category-permission");
            }
            
        }

        public static class Search {

            public static String key() {
                return Menu.key() + ".get";
            }

            public static LangMessage added() {
                return Lang.get(key() + ".added");
            }

            public static LangMessage notEnoughMoney() {
                return Lang.get(key() + ".not-enough-money");
            }

            public static LangMessage transactionError() {
                return Lang.get(key() + ".transaction-error");
            }

            public static LangMessage categoryPermission() {
                return Lang.get(key() + ".category-permission");
            }

        }
        
        public static class Remove {
            
            public static String key() {
                return Menu.key() + ".remove";
            }
            
            public static LangMessage open() {
                return Lang.get(key() + ".open");
            }
            
            public static LangMessage removed() {
                return Lang.get(key() + ".removed");
            }
            
        }
        
        public static class Rename {
            
            public static String key() {
                return Menu.key() + ".rename";
            }
            
            public static LangMessage open() {
                return Lang.get(key() + ".open");
            }
            
            public static LangMessage renamed() {
                return Lang.get(key() + ".renamed");
            }
            
        }
        
        public static class Cost {
            
            public static String key() {
                return Menu.key() + ".cost";
            }
            
            public static LangMessage open() {
                return Lang.get(key() + ".open");
            }
            
            public static LangMessage setCost() {
                return Lang.get(key() + ".set-cost");
            }
            
        }
        
        public static class Id {
            
            public static String key() {
                return Menu.key() + ".id";
            }
            
            public static LangMessage open() {
                return Lang.get(key() + ".open");
            }
            
            public static LangMessage clicked() {
                return Lang.get(key() + ".clicked");
            }
            
        }
        
    }
    
    public static class Command {
        
        public static String key() {
            return "command";
        }
        
        public static class Errors {
            
            public static String key() {
                return Command.key() + ".errors";
            }
            
            public static LangMessage noPermission() {
                return Lang.get(key() + ".no-permission");
            }
            
            public static LangMessage invalidArgs() {
                return Lang.get(key() + ".invalid-arguments");
            }
            
            public static LangMessage mustBePlayer() {
                return Lang.get(key() + ".must-be-player");
            }
            
            public static LangMessage integer() {
                return Lang.get(key() + ".integer");
            }
            
            public static LangMessage number() {
                return Lang.get(key() + ".number");
            }
            
            public static LangMessage negative() {
                return Lang.get(key() + ".negative");
            }
            
        }
        
        public static class Help {
            
            public static String key() {
                return Command.key() + ".help";
            }
            
            public static LangMessage header() {
                return Lang.get(key() + ".header");
            }
            
            public static LangMessage line() {
                return Lang.get(key() + ".line");
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Get {
            
            public static String key() {
                return Command.key() + ".get";
            }
            
            public static LangMessage headName() {
                return Lang.get(key() + ".head-name");
            }
            
            public static LangMessage oldMethod() {
                return Lang.get(key() + ".old-method");
            }
            
            public static LangMessage adding() {
                return Lang.get(key() + ".adding");
            }
            
            public static LangMessage fetching() {
                return Lang.get(key() + ".fetching");
            }
            
            public static LangMessage cantFind() {
                return Lang.get(key() + ".cant-find");
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Random {
            
            public static String key() {
                return Command.key() + ".random";
            }
            
            public static LangMessage noHeads() {
                return Lang.get(key() + ".no-heads");
            }
            
            public static LangMessage giving() {
                return Lang.get(key() + ".giving");
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Add {
            
            public static String key() {
                return Command.key() + ".add";
            }
            
            public static LangMessage notSupported() {
                return Lang.get(key() + ".not-supported");
            }
            
            public static LangMessage categoryLength() {
                return Lang.get(key() + ".category-length");
            }
            
            public static LangMessage adding() {
                return Lang.get(key() + ".adding");
            }
            
            public static LangMessage fetching() {
                return Lang.get(key() + ".fetching");
            }
            
            public static LangMessage cantFind() {
                return Lang.get(key() + ".cant-find");
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Hand {
            
            public static String key() {
                return Command.key() + ".hand";
            }
            
            public static LangMessage notSupported() {
                return Lang.get(key() + ".not-supported");
            }
            
            public static LangMessage noTextureProperty() {
                return Lang.get(key() + ".no-texture-property");
            }
            
            public static LangMessage noNameProperty() {
                return Lang.get(key() + ".no-name-property");
            }
            
            public static LangMessage notSkull() {
                return Lang.get(key() + ".not-skull");
            }
            
            public static LangMessage categoryLength() {
                return Lang.get(key() + ".category-length");
            }
            
            public static LangMessage adding() {
                return Lang.get(key() + ".adding");
            }
            
            public static LangMessage fetching() {
                return Lang.get(key() + ".fetching");
            }
            
            public static LangMessage cantFind() {
                return Lang.get(key() + ".cant-find");
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Give {
            
            public static String key() {
                return Command.key() + ".give";
            }
            
            public static LangMessage cantFindPlayer() {
                return Lang.get(key() + ".cant-find-player");
            }
            
            public static LangMessage cantFindHead() {
                return Lang.get(key() + ".cant-find-head");
            }
            
            public static LangMessage give() {
                return Lang.get(key() + ".give");
            }
            
            public static LangMessage invalidAmount() {
                return Lang.get(key() + ".invalid-amount");
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class OpenMenu {
            
            public static String key() {
                return Command.key() + ".open-menu";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Remove {
            
            public static String key() {
                return Command.key() + ".remove";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Rename {
            
            public static String key() {
                return Command.key() + ".rename";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }

        public static class Search {

            public static String key() {
                return Command.key() + ".search";
            }

            public static LangMessage found() {
                return Lang.get(key() + ".found");
            }

            public static LangMessage noneFound() {
                return Lang.get(key() + ".none-found");
            }

            public static HelpSection help() {
                return new HelpSection(key());
            }

        }
        
        public static class Cost {
            
            public static String key() {
                return Command.key() + ".cost";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Id {
            
            public static String key() {
                return Command.key() + ".id";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
    }
    
}
