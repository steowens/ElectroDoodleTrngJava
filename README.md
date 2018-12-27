# ElectroDoodleTrngJava
Key to ensuring cryptographic security is the use of a truly random number generator.  With software PRNG's you can never be really quite sure whether or not there is a back door in the code, unless you read every line.  This is why some people are opting for hardware random number generation.

In keeping with the above, I bought an [ElectroDoodle Trng off of Amazon](https://www.amazon.com/Random-Number-Generator-TRNG-N1-ElectroDoodle/dp/B07BRWVC5R/ref=sr_1_2?ie=UTF8&qid=1545423956&sr=8-2&keywords=trng), and they sent it to me along with a link to their defunct website for instructions on how to use.  So I had to figure out how to use it without instructions, this project shows how.

The product is great if you know how to use it, but without instructions it really isn't helpful to have such a device. The support for the TRNG is abysmal, but the hardware seems to work well. Hopefully this project will save you some frustration if you decide to use one of these devices to generate randomness for your encryption needs.

If you want to use this as a utility to generate randomness then you can do the following to install on your system

1) Create a directory in your home 

	$ mkdir ~/.bin

2) Copy the trng-random file from src/main/scripts to ~/.bin

	$ cp src/main/scripts/trng-random ~/.bin/
	
3) Build the project

	$ mvn install 
	
4) Copy the target/Trng.jar file to ~/.bin

	$ cp target/Trng.jar ~/.bin
	
5) Make ~/.bin/make-random executable 

    $ chmod +x ~/.bin/trng-random
    
6) Update your path to include ~/.bin

	echo "export PATH=$PATh:~/.bin" >> ~/.bash_profile

You can now execute 

    $ trng-random [--length nnnnn] --output-file outputFilePath


Now that you have the ability to create randomness in a file you can use this as a source of entropy for OpenSSL.

    $ trng-random --output-file entropy.bin --length 4096
    Creating randomness file: entropy.bin
    Progress indicator '.' == 1024 bytes:
    ....
    Wrote 4096 bytes of randomness to entropy.bin
    Finished.
    $ openssl genrsa -out my.key -aes256 -rand entropy.bin 4096
    4096 semi-random bytes loaded
    Generating RSA private key, 4096 bit long modulus
    ............++
    .........................................................................................++
    e is 65537 (0x10001)
    Enter pass phrase for my.key:
    Verifying - Enter pass phrase for my.key:
    
    
    
    
    