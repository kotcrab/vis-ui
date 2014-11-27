import fnmatch
import os

# config
main_dir = os.path.dirname(os.getcwd())
src_dir = os.path.join(main_dir, 'src', 'pl', 'kotcrab', 'vis')
header = os.path.join(main_dir, 'tools', 'license.txt')
# config end

f = open(header)
header_lines = f.readlines()
f.close()

verbose = False

def contains(small, big):
    if(len(small) > len(big)):
        return False
    
    for i in xrange(len(small) - 1):
        if not small[i] == big[i]:
            return False
        
    return True

def process_file(file_path):
    global missing
    file = open(file_path)
    source = file.readlines()
    file.close()
    
    if not contains(header_lines, source):
        missing += 1
        if verbose:
            print 'WARNING: Missing header: ' + file_path

missing = 0

def count_missing_headers():
    
    for root, dirnames, files in os.walk(src_dir):
           for filename in files:
                if filename.endswith(('.java', '.xtend')):
                    process_file(os.path.join(root, filename))
    return missing

def main():
    global verbose
    verbose = True
    count_missing_headers()
    print 'Done, missing: ' + str(missing)
    
if __name__ == "__main__":
    main()
