(chapter "Church Encoding")

(section "Numbers")
(example "zero and succ"
         (def zero (fn [f] (fn [x] x)))
         (defn succ [n] (fn [f] (fn [x] (f ((n f) x))))))
(example "Conversion to integer"
         (defn to-int [n] ((n (partial + 1)) 0)))
(example "Values"
         (def one (succ zero))
         (def two (succ one))
         (def three (succ two))
         (to-int zero)
         (to-int one)
         (to-int two)
         (to-int three))
(example "Addition"
         (defn n+ [a b] (fn [f] (comp (a f) (b f))))
         (to-int (n+ zero zero))
         (to-int (n+ two three)))
(example "Multiplication"
         (defn n* [a b] (comp a b))
         (to-int (n* zero zero))
         (to-int (n* two three)))
(example "Power"
         (defn n** [a b] (b a))
         (to-int (n** zero zero))
         (to-int (n** two three)))
(example "Predecessor"
         (defn pred' [f] (fn [[a b]] [(f a) a]))
         (defn pred [n]
           (fn [f] (fn [x]
                     (((n (pred' f)) [x x]) 1))))
         (to-int (pred three))
         (to-int (pred two))
         (to-int (pred one))
         (to-int (pred zero)))
(example "Subtraction"
         (defn n- [a b] ((b pred) a))
         (to-int (n- three one))
         (to-int (n- three two))
         (to-int (n- one one))
         (to-int (n- one three)))

(section "Pairs")
(example "Basic declarations"
         (def pair (fn [f] (fn [s] (fn [p] ((p f) s)))))
         (def fst (fn [p] (p (fn [f] (fn [s] f)))))
         (def snd (fn [p] (p (fn [f] (fn [s] s))))))
(example "Instance"
         (def pp ((pair 10) 20))
         pp
         (fst pp)
         (snd pp))

(section "Booleans")
(example "Values"
         (defn b-true [f s] f)
         (defn b-false [f s] s))
(example "Conversion to ordinary Booleans"
         (defn to-boolean [b] (b true false))
         (to-boolean b-true)
         (to-boolean b-false))
(example "Not"
         (defn b-not [b] (fn [f s] (b s f)))
         (to-boolean (b-not b-true))
         (to-boolean (b-not b-false)))
(example "And"
         (defn b-and [b1 b2] (fn [f s] (b1 (b2 f s) s)))
         (to-boolean (b-and b-false b-false))
         (to-boolean (b-and b-true b-false))
         (to-boolean (b-and b-false b-true))
         (to-boolean (b-and b-true b-true)))
(example "Or"
         (defn b-or [b1 b2] (fn [f s] (b1 f (b2 f s))))
         (to-boolean (b-or b-false b-false))
         (to-boolean (b-or b-true b-false))
         (to-boolean (b-or b-false b-true))
         (to-boolean (b-or b-true b-true)))

(section "Predicates")
(example "= 0"
         (defn is-zero? [n] ((n (fn [x] b-false)) b-true))
         (to-boolean (is-zero? zero))
         (to-boolean (is-zero? one))
         (to-boolean (is-zero? three)))
(example "<="
         (defn less-or-equal? [n1 n2] (is-zero? (n- n1 n2)))
         (to-boolean (less-or-equal? one three))
         (to-boolean (less-or-equal? one one))
         (to-boolean (less-or-equal? three one)))
(example "=="
         (defn equal? [n1 n2] (b-and (less-or-equal? n1 n2) (less-or-equal? n2 n1)))
         (to-boolean (equal? one three))
         (to-boolean (equal? one one))
         (to-boolean (equal? three one)))

(section "Signed numbers")
(example "Basic definitions"
         (defn signed
           ([n] ((pair n) zero))
           ([p n] ((pair p) n)))
         (defn negate [n] ((pair (snd n)) (fst n))))
(example "Conversion to integer"
         (defn signed-to-int [p] (((fst p) #(+ % 1)) (((snd p) #(- % 1)) 0)))
         (signed-to-int (signed zero))
         (signed-to-int (signed two))
         (signed-to-int (negate (signed zero)))
         (signed-to-int (negate (signed two))))
(example "Addition"
         (defn s+ [a b] ((pair (n+ (fst a) (fst b))) (n+ (snd a) (snd b))))
         (signed-to-int (s+ (signed one) (signed two)))
         (signed-to-int (s+ (signed one) (negate (signed two)))))
(example "Subtraction"
         (defn s- [a b] (s+ a (negate b)))
         (signed-to-int (s- (signed one) (signed two)))
         (signed-to-int (s- (signed one) (negate (signed two)))))
(example "Multiplication"
         (defn s* [a b]
           (let [a1 (fst a) a2 (snd a) b1 (fst b) b2 (snd b)]
             (signed
               (n+ (n* a1 b1) (n* b1 b2))
               (n+ (n* a1 b2) (n* a2 b1)))))
         (signed-to-int (s* (signed three) (signed two)))
         (signed-to-int (s* (signed three) (negate (signed two)))))
