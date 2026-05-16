library TestNewAttributes

using QDM version '5.7'

valueset "Active Condition": 'TBD'
valueset "Confirmed Status": 'TBD'
valueset "Penicillin Allergy": 'TBD'
valueset "Diabetes": 'TBD'

context Patient

/*
New in QDM 5.7: clinicalStatus and verificationStatus on Diagnosis

clinicalStatus indicates whether the condition is active, relapse, remission, or resolved.
verificationStatus indicates the degree of confidence in the assertion.
*/
define "Active Confirmed Diabetes":
  ["Diagnosis": "Diabetes"] D
    where D.clinicalStatus in "Active Condition"
      and D.verificationStatus in "Confirmed Status"

/*
New in QDM 5.7: verificationStatus on AllergyIntolerance

verificationStatus confirms whether the allergy/intolerance is confirmed or unconfirmed.
*/
define "Confirmed Penicillin Allergy":
  ["Allergy/Intolerance": "Penicillin Allergy"] A
    where A.verificationStatus in "Confirmed Status"

define "Active Diagnoses With Clinical Status":
  ["Diagnosis"] D
    where D.clinicalStatus is not null
