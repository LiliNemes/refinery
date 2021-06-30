import React from 'react';
import { render } from 'react-dom';

import XtextCodeMirror from "./components/XtextCodeMirror";

import '../css/index.scss';

const initialValue = `class Family {
  contains Person[] members
}

class Person {
  Person[] children opposite parent
  Person[0..1] parent opposite children
  int age
  TaxStatus taxStatus
}

enum TaxStatus {
  child, student, adult, retired
}

% A child cannot have any dependents.
error invalidTaxStatus(Person p) :-
  taxStatus(p, child), children(p, _q).

Family('family').
members('family', anne).
members('family', bob).
members('family', ciri).
children(anne, ciri).
?children(bob, ciri).
taxStatus(anne, adult).
age(anne, 35).
bobAge: 27.
age(bob, bobAge).
!age(ciri, bobAge).

scope Family = 1, Person += 5..10.
`;
const app = <XtextCodeMirror initialValue={initialValue}/>;

render(app, document.getElementById('app'));
