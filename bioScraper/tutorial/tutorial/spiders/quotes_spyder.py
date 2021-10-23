import scrapy
import urllib

class QuotesSpider(scrapy.Spider):
    name = "quotes"

    

    start_urls = [
        #'https://www.tudespensa.com/comprar/aceites-vinagres-y-condimentos-online/',
        #'https://www.tudespensa.com/comprar/caldos-sopas-y-pures-online/',
        #'https://www.tudespensa.com/comprar/bebidas-online/',
        #'https://www.tudespensa.com/comprar/charcuteria-y-quesos-online/',
        #'https://www.tudespensa.com/comprar/huevos-y-lacteos-online/',
        #'https://www.tudespensa.com/comprar/azucar-edulcorantes/',
        'https://www.tudespensa.com/comprar/salsas-y-mayonesas-online/'
    ]

    def analyze_product(self, ingredients):
        mark = 10;
        info = "";
        # allIngredients = ['agua carbonatada', 'azucar', 'e150d', 'glutamato', 'grasa de palma', 'Aceite de palma'];
        # match = [];
        # for i in allIngredients:
        #     if i in ingredients.lower():
        #         match.append(i);
        #         print i;
        # print("MAAAAATCH");
        # print(match);

        if "aceite de palma" in ingredients:
            mark = mark - 6;
            info = "El consumo de aceite de palma aumenta los niveles de colesterol y puede contribuir al desarrollo de problemas cardiovasculares."

        if "glutamato" in ingredients.lower():
            mark = mark - 6;
            info = info + "\n" + "El glutamato puede causar dolores de cabeza, migranas, espasmos musculares, nausea, alergias, anafilaxis, ataques epilepticos, depresion e irregularidades cardiacas.";

        mi = [mark, info];
        return mi;

    def parse(self, response):

        for href in response.css("div.c-cat-product-item__data a::attr(href)").extract():
            yield scrapy.Request(response.urljoin(href),
                                callback=self.parse_product)

    
    def parse_product(self, response):
        with open('productsInfo.txt', 'r') as myfile:
            data=myfile.read().replace('\n', '')

        description = response.xpath("//meta[@name='description']/@content")[0];
        barcodes = description.re('(\w+)$');
        if not barcodes:
            print('Empty barcode');
        else:
            code = barcodes[0].decode('unicode-escape');
            data = data.decode('unicode-escape');
            if code in data:
                print('Product already registered');
                #Maybe we should not check that because if the ingredients change the information should be updated
            else: 
                print('code:'+code);
                imageLink = response.xpath("//img[@class='c-cat-product-detail__more-imgs']/@src").extract_first();
                print('imageLink: '+imageLink);
                urllib.urlretrieve(imageLink, "images/"+code+".jpg");

                descriptionString = description.extract();
                print('descriptionString:'+descriptionString);
                name = descriptionString.split("en TuD")[0];
                print('name'+name);
                ingredients = response.xpath("//p[@class='c-nbmlc-ficha-producto__description-txt']/text()").extract_first();
                if ingredients is None:
                    ingredients = 'Empty';

                print('ingredients:'+ingredients);
                nutritionalInfoList = response.xpath("//div[@class='c-nbmlc-ficha-producto__inforn-propn']//span/text()").extract();
                nutritionalInfo = u''.join(nutritionalInfoList);
                if nutritionalInfo is None:
                    nutritionalInfo = 'Empty';
                print('nutritionalInfo: '+nutritionalInfo);

                filename = 'productsInfo.txt'
                mi = self.analyze_product(ingredients);
                product = code+"; "+name+"; "+ingredients+"; "+nutritionalInfo+"; "+str(mi[0])+"; "+mi[1]+"; ";
                product = product.encode('utf8');

                with open(filename, 'a') as f:
                    f.write(product+"\n")


    


