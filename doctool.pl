#!perl

# ewpratten's javadoc tool
# By: Evan Pratten <ewpratten@retrylife.ca>

use 5.010;
use strict;
use warnings;

# Handle CLI flags
my ($mode, $setting) = @ARGV;

# Ensure we actually have flags
if (not defined $mode){
    print "No program arguments specified. \nUse one of -l (local) or -p (publish) to specify mode.\n";
    exit 1;
} else {
    print "Starting javadoc tool\n";
}

# No matter the mode, we must build the documentation first
print "Building javadoc\n";
system("./gradlew javadoc --console=plain");

# Deal with javascript bug
print "Injecting javascript search bugfix\n";
system("sed -i 's/useModuleDirectories/false/g' build/reports/docs/search.js");

# Handle local hosting
if ($mode eq "-l"){
    print("Starting local javadoc server\n");
    print("This will move to the documentation directory\n");
    
    # Move to docs directory
    chdir "./build/reports/docs";

    # Start http server
    system("python -m SimpleHTTPServer 5806");

} elsif ($mode eq "-p"){
    print "Publishing documentation\n";

    # Read the current git origin URL
    my $origin = `git config --get remote.origin.url`;
    chomp $origin;

    # Read the repo name and dir
    my $name = `basename \`git rev-parse --show-toplevel\``;
    my $dir = `pwd`;
    chomp $name;
    chomp $dir;

    print "Detected repo settings (Name: '$name', URL: '$origin')\n";

    # Clone a copy of the current repo, one level up
    chdir "../";
    system("git clone '$origin' '$name-DOBLE_TMP'");
    chdir "$name-DOBLE_TMP";

    # Checkout the latest gh-pages branch (Redundant)
    # system("git checkout -b gh-pages");
    my $fail = system("git checkout gh-pages");

    if ($fail){
        print "Could not checkout gh-pages\n";
        exit 1;
    }

    # Determine date string for branch name
    my $date = `date +"%I_%M-%h%d"`;
    chomp $date;

    # Checkout a new branch for this docs release
    my $new_branch = "javadoc-doble-$date";
    system("git checkout -b $new_branch");

    # Remove all branch contents
    system("rm -rf ./*");

    system("git add .");
    system("git commit -m \"Auto-authored by doble. Branch cleared\"");

    # Copy all documentation over to new branch
    system("cp -r $dir/build/reports/docs/* .");

    # Add all and make a commit for the new changes
    system("git add .");
    system("git commit -m \"Auto-authored by doble. Javadocs have been updated\"");

    # Push the changes
    system("git push --set-upstream origin $new_branch");
    

} else {
    print "Invalid program arguments. Stopping\n";
    exit 1;

}