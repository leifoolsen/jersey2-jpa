package com.github.leifoolsen.jerseyjpa.util;

import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class DomainPopulator {

    public static final String ALMA_BOOKS    = "18468";
    public static final String DAMM          = "82040";
    public static final String TURNER        = "11181";
    public static final String HISTORY_PRESS = "07524";
    public static final String PAX           = "82530";
    public static final String GYLDENDAL     = "82054";
    public static final String VINTAGE       = "17847";
    public static final String VINTAGE_UK    = "00995";
    public static final String CAPPELEN      = "82021";
    public static final String CAPPELEN_DAMM = "82022";
    public static final String PICADOR       = "14472";
    public static final String WEIDENFELD    = "02978";
    public static final String DOUBLEDAY     = "08575";
    public static final String PENGUIN       = "02419";

    private DomainPopulator() {}

    public static Map<String, Publisher> getPublishers() {

        // Add some publishers
        final Map<String, Publisher> publishers = Maps.newHashMap();
        publishers.put(ALMA_BOOKS   , new Publisher(ALMA_BOOKS, "Alma Books"));
        publishers.put(DAMM         , new Publisher(DAMM, "Damm"));
        publishers.put(TURNER       , new Publisher(TURNER, "Turner Publishing Company"));
        publishers.put(HISTORY_PRESS, new Publisher(HISTORY_PRESS, "The History Press"));
        publishers.put(PAX          , new Publisher(PAX, "Pax"));
        publishers.put(GYLDENDAL    , new Publisher(GYLDENDAL, "Gyldendal"));
        publishers.put(VINTAGE      , new Publisher(VINTAGE, "Vintage Books"));
        publishers.put(VINTAGE_UK   , new Publisher(VINTAGE_UK, "Vintage Books UK"));
        publishers.put(CAPPELEN     , new Publisher(CAPPELEN, "Cappelen"));
        publishers.put(CAPPELEN_DAMM, new Publisher(CAPPELEN_DAMM, "Cappelen Damm"));
        publishers.put(PICADOR      , new Publisher(PICADOR, "Picador"));
        publishers.put(WEIDENFELD   , new Publisher(WEIDENFELD, "Weidenfeld & Nicolson (Orion Publishing Co)"));
        publishers.put(DOUBLEDAY    , new Publisher(DOUBLEDAY, "Doubleday (Transworld Publishers Ltd)"));
        publishers.put(PENGUIN      , new Publisher(PENGUIN, "Penguin Books Ltd"));

        return publishers;
    }

    public static List<Book> getBooks(final Map<String, Publisher> publishers) {

        // Add some books
        final List<Book> books = Arrays.asList(
            Book.with("9781846883668")
                    .title("Travelling to Infinity: The True Story")
                    .author("Hawking, Jane")
                    .publisher(publishers.get(ALMA_BOOKS))
                    .published(new GregorianCalendar(2014, 12, 18).getTime())
                    .summary("Soon to be a major motion picture starring Eddie Redmayne as Hawking and Felicity " +
                            "Jones as his wife Jane. It chronicles their relationship, from his early development " +
                            "of ALS to his success in physics.")
                    .build(),

            Book.with("9788204094261")
                    .title("Superstjernen Stephen Hawking: biografi")
                    .author("Gribbin, John; White, Michael")
                    .publisher(publishers.get(DAMM))
                    .published(new GregorianCalendar(2014, 1, 1).getTime())
                    .translator("Larsen, Anne Kirsti Solheim; Larsen, Finn B.")
                    .summary("Boka forteller om livet til vitenskapsmannen Stephen Hawking. Boka gir ett innblikk i både " +
                            "privatlivet til Hawking, og i hans karriere som forsker innenfor teoretisk fysikk og " +
                            "astrofysikk. Den setter vitenskapen inn i en menneskelig sammenheng, og viser hvordan " +
                            "vitenskap og liv er uløselig knytta sammen for Hawking. Har register.")
                    .build(),

            Book.with("9781118175392")
                    .title("Alone in the Universe: Why Our Planet Is Unique")
                    .author("Gribbin, John")
                    .publisher(publishers.get(TURNER))
                    .published(new GregorianCalendar(2011, 1, 1).getTime())
                    .summary("The acclaimed author of In Search of Schrodinger's Cat searches for life on other " +
                            "planets. Are we alone in the universe? Surely amidst the immensity of the cosmos there must " +
                            "be other intelligent life out there. Don't be so sure, says John Gribbin, one of today's " +
                            "best popular science writers. In this fascinating and intriguing new book, Gribbin argues " +
                            "that the very existence of intelligent life anywhere in the cosmos is, from an " +
                            "astrophysicist's point of view, a miracle. So why is there life on Earth and (seemingly) " +
                            "nowhere else? What happened to make this planet special? Taking us back some 600 million " +
                            "years, Gribbin lets you experience the series of unique cosmic events that were " +
                            "responsible for our unique form of life within the Milky Way Galaxy.Written by one of " +
                            "our foremost popular science writers, author of the bestselling In Search of Schrodinger's " +
                            "Cat Offers a bold answer to the eternal question, \"Are we alone in the universe?\"" +
                            "Explores how the impact of a quote; with Venus 600 million years ago created our moon, " +
                            "and along with it, the perfect conditions for life on EarthFrom one of our most talented " +
                            "science writers, this book is a daring, fascinating exploration into the dawning of the " +
                            "universe, cosmic collisions and their consequences, and the uniqueness of life on Earth.")
                    .build(),

            Book.with("9780752495620")
                    .title("Guide to Middle Earth: Tolkien and The Lord of the Rings")
                    .author("Duriez, Colin")
                    .publisher(publishers.get(HISTORY_PRESS))
                    .published(new GregorianCalendar(2013, 1, 1).getTime())
                    .summary("An illuminating guide to Middle-earth and the man who created it.")
                    .build(),

            Book.with("9788253019727")
                    .title("Vredens duer")
                    .author("Steinbeck, John")
                    .publisher(publishers.get(PAX))
                    .published(new GregorianCalendar(1998, 1, 1).getTime())
                    .translator("Omre, Arthur")
                    .summary("I 1939 (på norsk i 1940) kom denne romanen som slo fast at Steinbeck var en av mestrene " +
                            "i moderne amerikansk prosadiktning. Romanen står i dag som et minnesmerke over " +
                            "depresjonens elendighet. Handlingen er hentet fra 1930-årenes USA. Hovedpersonene er " +
                            "familien Joad, en av mange farmerfamilier som må flykte fra Oklahoma, etter at banker og " +
                            "storspekulanter har tatt jorda fra dem. Lokket av løfter fra farmere i California begir de " +
                            "seg vestover mot det forjettede land, der ny elendighet venter dem.")
                    .build(),

            Book.with("9788205478428") // 9788205418820     -  9788205478428
                    .title("Gravrøys")
                    .author("Theorin, Johan")
                    .publisher(publishers.get(GYLDENDAL))
                    .published(new GregorianCalendar(2014, 1, 1).getTime())
                    .translator("Bolstad, Kari")
                    .summary("I Johan Theorins fjerde bok fra Öland forenes fortid og nåtid i en særdeles " +
                            "velskrevet og ubehagelig krim. Det er midtsommer på Öland. Tusenvis av turister er " +
                            "kommet til den kalkhvite øya, der sommersolen steker, men ferieparadiset skjuler " +
                            "mørke hemmeligheter. En av de besøkende har kommet tilbake for å kreve betaling for " +
                            "gammel gjeld. Han etterlater seg død og skrekk i sommernatten. Ingen vet hvem han " +
                            "er, eller hva han vil. Men det er én mann som begynner å ane uråd. Gerlof Davidsson, " +
                            "en av øyas eldste innbyggere, begynner å forstå hvem den ukjente er, og hvorfor han " +
                            "søker hevn. Han har nemlig møtt mannen før, i ungdomstiden, da de begge sto på " +
                            "kirkegården og plutselig hørte banking fra en kiste ? I Johan Theorins fjerde bok " +
                            "fra Öland, Gravrøys, forenes fortid og nåtid i en særdeles velskrevet og " +
                            "ubehagelig krim.")
                    .build(),

            Book.with("9788205478336")
                    .title("Kuppet: på innsiden av Norges mektigste mafiafamilie")
                    .author("Aass, Hans Petter; Widerøe, Rolf J.")
                    .publisher(publishers.get(GYLDENDAL))
                    .published(new GregorianCalendar(2015, 1, 1).getTime())
                    .summary("En ny, knallsterk dokumentar fra forfatterne av bestselgerne Dødsranet og Krigshelten. " +
                            "Tema: Nordea-kuppet, norgeshistoriens frekkeste bedrageri. 20. juli 2010 tropper en 49 " +
                            "år gammel hjelpepleier opp i Nordeas filialer på Tveita i Oslo. Utkledd som " +
                            "millionærarving og med falskt bankkort ber hun en velvillig saksbehandler om å " +
                            "registrere en fullmakt på en ung mann i hennes følge. Snart er 62 millioner kroner " +
                            "overført til flere konti i Dubai. Pengene er aldri kommet til rette. Hovedmannen går " +
                            "fortsatt fri. Forfatterne går tett på den norsk-pakistanske familien bak bedrageriet, " +
                            "av politiet utpekt som den mektigste mafiafamilien i Norge. I kretsen rundt dem dukker " +
                            "det opp flere sentrale personer fra Norges tyngste kriminelle miljøer ? fra «B-gjengen», " +
                            "kriminelle MC-bander, ransmiljøet og torpedomiljøet. Det er nådeløse oppgjør, " +
                            "luksusliv i Dubai og pengestrømmer på ville veier. I Oslos underverden foregår det " +
                            "ting du aldri ville ha trodd.")
                    .build(),

            Book.with("9788205418820")
                    .title("Gyldendals store fugleguide: Europas og middelhavsområdets fugler i felt")
                    .author("Svensson, Lars")
                    .publisher(publishers.get(GYLDENDAL))
                    .published(new GregorianCalendar(2011, 1, 1).getTime())
                    .translator("Sandvik, Jostein; Syvertsen, Per Ole")
                    .summary("Verdens beste fuglebok i ny feltutgave! Gyldendals store fugleguide er markedets mest " +
                            "omfattende felthåndbok og anses som et normgivende standardverk for fuglehåndbøker. " +
                            "Fugleguiden gjennomgikk en omfattende revidering i 2010. Omfanget ble økt med 48 sider. " +
                            "Ca.20 nye bildesider kom til, mens andre fikk vesentlige tillegg. Arter, slekter og " +
                            "familier er nå ordnet ut fra de siste vitenskapelige erfaringene. I boken blir ca. 900 " +
                            "arter behandlet, derav 800 utførlig. De grundige tekstene er helt oppdatert og i mange " +
                            "tilfeller helt nyskrevne. Her beskrives hver arts størrelse,biotopvalg, kjennetegn og " +
                            "variasjoner. Symbolene angir hvor vanlige eller sjeldne artene er. De oversiktlige " +
                            "kartene viser utbredelsen. For alle som er interessert i fugler, er denne boken like " +
                            "uunnværlig som kikkerten. Redaktør for den norske utgaven er Viggo Ree. Boken er nå " +
                            "innbundet i en feltmessig softcoverutgave.")
                    .build(),

            Book.with("9781784700089")
                    .title("Alan Turing: The Enigma")
                    .author("Hodges, Andrew")
                    .publisher(publishers.get(VINTAGE_UK))
                    .published(new GregorianCalendar(2014, 11, 13).getTime())
                    .summary("This is the official book that inspired the film The Imitation Game, which stars " +
                            "Benedict Cumberbatch and Keira Knightley, and which has received eight Oscar " +
                            "nominations, including: Best film; Best Actor in a Leading Role; Best Supporting " +
                            "Actress; Best Adapted Screenplay; and Alan Turing was the mathematician whose " +
                            "cipher-cracking transformed the Second World War. Taken on by British Intelligence " +
                            "in 1938, as a shy young Cambridge don, he combined brilliant logic with a flair for " +
                            "engineering. In 1940 his machines were breaking the Enigma-enciphered messages of " +
                            "Nazi Germany's air force. He then headed the penetration of the super-secure " +
                            "U-boat communications. But his vision went far beyond this achievement. Before the " +
                            "war he had invented the concept of the universal machine, and in 1945 he turned t" +
                            "his into the first design for a digital computer. Turing's far-sighted plans for " +
                            "the digital era forged ahead into a vision for Artificial Intelligence. However, " +
                            "in 1952 his homosexuality rendered him a criminal and he was subjected to humiliating " +
                            "treatment. In 1954, aged 41, Alan Turing took his own life.")
                    .build(),

            Book.with("9780099554486")
                    .title("The Lives of Others")
                    .author("Mukherjee, Neel")
                    .publisher(publishers.get(VINTAGE))
                    .published(new GregorianCalendar(2015, 1, 8).getTime())
                    .summary("This is book shortlisted for the Man Booker Prize 2014. It was shortlisted for the " +
                            "Costa Novel Award 2014. Calcutta, 1967. Unnoticed by his family, Supratik has become " +
                            "dangerously involved in extremist political activism. Compelled by an idealistic " +
                            "desire to change his life and the world around him, all he leaves behind before " +
                            "disappearing is a note. At home, his family slowly begins to unravel. Poisonous " +
                            "rivalries grow, the once-thriving family business implodes and destructive secrets " +
                            "are unearthed. And all around them the sands are shifting as society fractures, " +
                            "for this is a moment of turbulence, of inevitable and unstoppable change.")
                    .build(),

            Book.with("9788202148683")
                    .title("Fisken")
                    .author("Loe, Erlend")
                    .publisher(publishers.get(CAPPELEN))
                    .published(new GregorianCalendar(1994, 1, 1).getTime())
                    .summary("Kurt er truckfører. Hver dag kjører han truck nede på kaia, og han løfter kasser " +
                            "som veier over 1000 kilo. Kurt har dessuten bart, og en søt kone og tre rare barn. " +
                            "En dag finner Kurt noe på kaia. Han finner noe han aldri har sett før. " +
                            "Noe virkelig kjempestort.")
                    .build(),

            Book.with("9788202244699")
                    .title("Rumpemelk fra Afrika")
                    .author("Loe, Erlend")
                    .publisher(publishers.get(CAPPELEN_DAMM))
                    .published(new GregorianCalendar(2012, 9, 1).getTime())
                    .summary("Marko kjeder seg og er tørst. Men han vil ikke ha vanlig melk. Han vil ha rumpemelk " +
                            "fra Afrika. Mamma og pappa har aldri hørt om slik melk og ber ham om å slutte å " +
                            "tøyse. \"Da drar jeg til Afrika og henter rumpemelk selv,\" sier han. " +
                            "Så går han ut og tar Afrikabussen. Det er ikke lett å få tak i rumpemelk i Afrika " +
                            "heller. Alle Marko treffer, både folk og dyr, drikker et eller annet, men de er " +
                            "skikkelig hemmelighetsfulle. Det er i hvert fall ikke rumpemelk påstår de. " +
                            "Men Marko gir seg ikke, og etter en stund er han på vei hjem med en " +
                            "egen tankbil med rumpemelk.")
                    .build(),

            Book.with("9788202425975")
                    .title("Doppler")
                    .author("Loe, Erlend")
                    .publisher(publishers.get(CAPPELEN_DAMM))
                    .published(new GregorianCalendar(2013, 10, 1).getTime())
                    .summary("En samfunnskritisk roman om forbruk, eksistens ... og en elgkalv ved navn Bongo. " +
                            "Min far er død. Og i går tok jeg en elg av dage. Hva kan jeg si. Det var den eller " +
                            "meg. Doppler er en vellykket mann av sin tid. Familiefar med to barn, fint hus og " +
                            "god jobb som han drar til hver morgen i sin Volvo. En dag faller han av sykkelen " +
                            "på en tur i marka. Halvt svimeslått registrerer han en ro han ikke har kjent på " +
                            "lenge. Han slipper de trivielle tankene om nytt bad og valget om armatur og fliser. " +
                            "Borte er også den evinnelige surringen av barnesangene fra sønnens tallrike videoer, " +
                            "mens erkjennelsen om farens død blir tydeligere. Doppler forlater jobben, hjemmet " +
                            "og familien og flytter ut i skogen. Naturen er vakker, mørk og dyp og han finner " +
                            "selskap i en elgkalv han kaller Bongo. Han prøver å leve som jeger og sanker, " +
                            "men må erkjenne at når det gjelder behovet for skummet melk, må han ta steget " +
                            "videre til bytteøkonomi.")
                    .build(),

            Book.with("9788202426378")
                    .title("Naiv. Super")
                    .author("Loe, Erlend")
                    .publisher(publishers.get(CAPPELEN_DAMM))
                    .published(new GregorianCalendar(2013, 1, 1).getTime())
                    .summary("JEG GIR STORT SETT FAEN I ROM, MEN JEG HAR PROBLEMER MED TID.Den 25 år gamle " +
                            "hovedpersonen har to venner. En god og en dårlig. Og så har han en bror som ikke " +
                            "er så altfor sympatisk. Når denne broren slår ham i krokket, raser tilværelsen " +
                            "hans sammen. Han mister sin begeistring og troen på at alt vil gå bra til slutt og " +
                            "havner i en eksistensiell krise. Han bryter opp fra sitt vanlige liv, dropper " +
                            "studiene og låner brorens leilighet. Der tilbringer han tiden dels med å leke dels " +
                            "med å reflektere over ting han liker og misliker. Det hele er skildret med en " +
                            "særlig blanding av ironi og naivitet der en lengsel etter den tapte barndom ligger " +
                            "under. NAIV. SUPER. er en enkel historie om veldig kompliserte ting.")
                    .build(),

            Book.with("9788202365387")
                    .title("Fonk")
                    .author("Loe, Erlend")
                    .publisher(publishers.get(CAPPELEN_DAMM))
                    .published(new GregorianCalendar(2011, 10, 1).getTime())
                    .summary("Mens Fvonk står i disse, og andre, tanker ruller en helt ren sort bil, tyskprodusert, " +
                            "med sotede vinduer inn i oppkjørselen og stanser uten å slå av motoren. Bilen ser " +
                            "offisiell ut og Fvonk reagerer negativt, han vil ikke ha noe med myndigheter og gjøre, " +
                            "eller øvrigheten, særlig ikke den. Etter noen sekunder kommer en kvinne i slutten av " +
                            "femtiårene ut fra høyre bakdør, som for øvrig lager en dump, rytmisk varsellyd, bim, " +
                            "bim, bim, slik at ingen skal være i tvil om at den er åpen. Kvinnen lukker døren og " +
                            "ser rundt seg, som for å undersøke at hun ikke blir observert av andre enn Fvonk, det " +
                            "er bare de to som er i situasjonen, kvinnen og Fvonk. Fvonk har jobbet i Norges " +
                            "gang- og mosjonsforbund i forbundets vanskelige periode. Han har vært vitne til " +
                            "økonomisk rot og er sterkt preget av ukultur. Så får han en hybelboer som ikke er " +
                            "hvem som helst. Snarere tvert imot. Tilfeldigvis heter han Jens, og tilfeldigvis er " +
                            "han statsminister.")
                    .build(),

            Book.with("9781447268970")
                    .title("Station Eleven")
                    .author("St. John Mandel, Emily")
                    .publisher(publishers.get(PICADOR))
                    .published(new GregorianCalendar(2015, 1, 1).getTime())
                    .summary("DAY ONE The Georgia Flu explodes over the surface of the earth like a neutron bomb. " +
                            "News reports put the mortality rate at over 99%. WEEK TWO Civilization has crumbled. " +
                            "YEAR TWENTY A band of actors and musicians called the Travelling Symphony move through " +
                            "their territories performing concerts and Shakespeare to the settlements that have " +
                            "grown up there. Twenty years after the pandemic, life feels relatively safe. But now " +
                            "a new danger looms, and he threatens the hopeful world every survivor has tried to " +
                            "rebuild. STATION ELEVEN Moving backwards and forwards in time, from the glittering " +
                            "years just before the collapse to the strange and altered world that exists twenty " +
                            "years after, Station Eleven charts the unexpected twists of fate that connect six " +
                            "people: famous actor Arthur Leander; Jeevan - warned about the flu just in time; " +
                            "Arthur's first wife Miranda; Arthur's oldest friend Clark; Kirsten, a young actress " +
                            "with the Travelling Symphony; and the mysterious and self-proclaimed 'prophet'. " +
                            "Thrilling, unique and deeply moving, this is a beautiful novel that asks questions " +
                            "about art and fame and about the relationships that sustain us through anything - " +
                            "even the end of the world.")
                    .build(),

            Book.with("9781447279402")
                    .title("The Guest Cat")
                    .author("Hiraide, Takashi")
                    .publisher(publishers.get(PICADOR))
                    .published(new GregorianCalendar(2014, 9, 25).getTime())
                    .translator("Selland, Eric")
                    .summary("THE SUNDAY TIMES AND NEW YORK TIMES BESTSELLER. A couple in their thirties live in a " +
                            "small rented cottage in a quiet part of Tokyo. They work at home as freelance writers. " +
                            "They no longer have very much to say to one another. One day a cat invites itself " +
                            "into their small kitchen. She is a beautiful creature. She leaves, but the next day " +
                            "comes again, and then again and again. New, small joys accompany the cat; the days " +
                            "have more light and colour. Life suddenly seems to have more promise for the husband " +
                            "and wife; they go walking together, talk and share stories of the cat and its little " +
                            "ways, play in the nearby Garden. But then something happens that will change " +
                            "everything again. The Guest Cat is an exceptionally moving and beautiful novel " +
                            "about the nature of life and the way it feels to live it. Written by Japanese poet " +
                            "and novelist Takashi Hiraide, the book won Japan's Kiyama Shohei Literary Award, and " +
                            "was a bestseller in France and America.")
                    .build(),

            Book.with("9780241971987")
                    .title("Little Failure")
                    .author("Shteyngart, Gary")
                    .publisher(publishers.get(PENGUIN))
                    .published(new GregorianCalendar(2014, 10, 29).getTime())
                    .summary("Gary Shteyngart's parents dreamed that he would become a lawyer, or at least an " +
                            "accountant, something their distracted son was simply not cut out to do. Fusing " +
                            "English and Russian, his mother created the term Failurchka - 'Little Failure' - " +
                            "which she applied to her son. With love. Mostly. A candid and poignant story of a " +
                            "Soviet family's trials and tribulations, and of their escape in 1979 to the " +
                            "consumerist promised land of the USA, Little Failure is also an exceptionally " +
                            "funny account of the author's transformation from asthmatic toddler in Leningrad " +
                            "to 40 - something Manhattanite with a receding hairline and a memoir to write.")
                    .build()
        );

        return books;
    }
}
