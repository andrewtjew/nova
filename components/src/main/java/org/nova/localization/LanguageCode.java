package org.nova.localization;

import java.util.HashMap;

public enum LanguageCode
{
    ab(new Language_ISO_639_1("Abkhazian","ab")),
    aa(new Language_ISO_639_1("Afar","aa")),
    af(new Language_ISO_639_1("Afrikaans","af")),
    ak(new Language_ISO_639_1("Akan","ak")),
    sq(new Language_ISO_639_1("Albanian","sq")),
    am(new Language_ISO_639_1("Amharic","am")),
    ar(new Language_ISO_639_1("Arabic","ar")),
    an(new Language_ISO_639_1("Aragonese","an")),
    hy(new Language_ISO_639_1("Armenian","hy")),
    as(new Language_ISO_639_1("Assamese","as")),
    av(new Language_ISO_639_1("Avaric","av")),
    ae(new Language_ISO_639_1("Avestan","ae")),
    ay(new Language_ISO_639_1("Aymara","ay")),
    az(new Language_ISO_639_1("Azerbaijani","az")),
    bm(new Language_ISO_639_1("Bambara","bm")),
    ba(new Language_ISO_639_1("Bashkir","ba")),
    eu(new Language_ISO_639_1("Basque","eu")),
    be(new Language_ISO_639_1("Belarusian","be")),
    bn(new Language_ISO_639_1("Bengali (Bangla)","bn")),
    
    bh(new Language_ISO_639_1("Bihari","bh")),
    bi(new Language_ISO_639_1("Bislama","bi")),
    bs(new Language_ISO_639_1("Bosnian","bs")),
    br(new Language_ISO_639_1("Breton","br")),
    bg(new Language_ISO_639_1("Bulgarian","bg")),
    my(new Language_ISO_639_1("Burmese","my")),
    ca(new Language_ISO_639_1("Catalan","ca")),
    ch(new Language_ISO_639_1("Chamorro","ch")),
    ce(new Language_ISO_639_1("Chechen","ce")),
    ny(new Language_ISO_639_1("Chichewa, Chewa, Nyanja","ny")),
    
    zh(new Language_ISO_639_1("Chinese","zh")),
    zh_hans(new Language_ISO_639_1("Chinese (Simplified)","zh-Hans",null,"zh-Hans")),
    zh_hant(new Language_ISO_639_1("Chinese (Traditional)","zh-Hant",null,"zh-Hant")),
    cv(new Language_ISO_639_1("Chuvash","cv")),
    kw(new Language_ISO_639_1("Cornish","kw")),
    co(new Language_ISO_639_1("Corsican","co")),
    cr(new Language_ISO_639_1("Cree","cr")),
    hr(new Language_ISO_639_1("Croatian","hr")),
    cs(new Language_ISO_639_1("Czech","cs")),
    da(new Language_ISO_639_1("Danish","da")),
    dv(new Language_ISO_639_1("Divehi, Dhivehi, Maldivian","da")),
    nl(new Language_ISO_639_1("Dutch","nl")),
    dz(new Language_ISO_639_1("Dzongkha","dz")),
    en(new Language_ISO_639_1("English","en")),
    eo(new Language_ISO_639_1("Esperanto","eo")),
    et(new Language_ISO_639_1("Estonian","et")),
    ee(new Language_ISO_639_1("Ewe","ee")),
    fo(new Language_ISO_639_1("Faroese","fo")),
    fj(new Language_ISO_639_1("Fijian","fj")),
    fi(new Language_ISO_639_1("Finnish","fi")),
    fr(new Language_ISO_639_1("French","fr")),
//    ff(new Language_ISO_639_1("Fula","ff")),
    ff(new Language_ISO_639_1("Fulah","ff")),
//    ff(new Language_ISO_639_1("Pulaar","ff")),
//    ff(new Language_ISO_639_1("Pular","ff")),
    gl(new Language_ISO_639_1("Galician","gl")),
    gd(new Language_ISO_639_1("Gaelic, Scottish Gaelic","gd")),
    gv(new Language_ISO_639_1("Manx","gv")),
    de(new Language_ISO_639_1("German","de")),
    el(new Language_ISO_639_1("Greek","el")),
    gn(new Language_ISO_639_1("Guarani","gn")),
    gu(new Language_ISO_639_1("Gujarati","gu")),
    ht(new Language_ISO_639_1("Haitian Creole","ht")),
    ha(new Language_ISO_639_1("Hausa","ha")),
    he(new Language_ISO_639_1("Hebrew","he")),
    hz(new Language_ISO_639_1("Herero","hz")),
    hi(new Language_ISO_639_1("Hindi","hi")),
    ho(new Language_ISO_639_1("Hiri Motu","ho")),
    hu(new Language_ISO_639_1("Hungarian","hu")),
    is(new Language_ISO_639_1("Icelandic","is")),
    io(new Language_ISO_639_1("Ido","io")),
    ig(new Language_ISO_639_1("Igbo","ig")),
    id(new Language_ISO_639_1("Indonesian","id","in","id")),
    ia(new Language_ISO_639_1("Interlingua","ia")),
    ie(new Language_ISO_639_1("Interlingue","ie")),
    iu(new Language_ISO_639_1("Inuktitut","iu")),
    ik(new Language_ISO_639_1("Inupiak","ik")),
    ga(new Language_ISO_639_1("Irish","ga")),
    it(new Language_ISO_639_1("Italian","it")),
    ja(new Language_ISO_639_1("Japanese","ja")),
    jv(new Language_ISO_639_1("Javanese","jv")),
    ka(new Language_ISO_639_1("Georgian","ka")),
    kl(new Language_ISO_639_1("Kalaallisut, Greenlandic","kl")),
    kn(new Language_ISO_639_1("Kannada","kn")),
    kr(new Language_ISO_639_1("Kanuri","kr")),
    ks(new Language_ISO_639_1("Kashmiri","ks")),
    kk(new Language_ISO_639_1("Kazakh","kk")),
    km(new Language_ISO_639_1("Khmer","km")),
    ki(new Language_ISO_639_1("Kikuyu","ki")),
    rw(new Language_ISO_639_1("Kinyarwanda (Rwanda)","rw")),
    rn(new Language_ISO_639_1("Kirundi","rn")),
    ky(new Language_ISO_639_1("Kyrgyz","ky")),
    kv(new Language_ISO_639_1("Komi","kv")),
    kg(new Language_ISO_639_1("Kongo","kg")),
    ko(new Language_ISO_639_1("Korean","ko")),
    ku(new Language_ISO_639_1("Kurdish","ku")),
    kj(new Language_ISO_639_1("Kwanyama","kj")),
    lo(new Language_ISO_639_1("Lao","lo")),
    la(new Language_ISO_639_1("Latin","la")),
    lv(new Language_ISO_639_1("Latvian (Lettish)","lv")),
    li(new Language_ISO_639_1("Limburgish (Limburger)","li")),
    ln(new Language_ISO_639_1("Lingala","ln")),
    lt(new Language_ISO_639_1("Lithuanian","lt")),
    lu(new Language_ISO_639_1("Luga-Katanga","lu")),
    lg(new Language_ISO_639_1("Luganda Ganda","lg")),
    lb(new Language_ISO_639_1("Luxembourgish","lb")),
    mk(new Language_ISO_639_1("Macedonian","mk")),
    mg(new Language_ISO_639_1("Malagasy","mg")),
    ms(new Language_ISO_639_1("Malay","ms")),
    ml(new Language_ISO_639_1("Malayalam","ml")),
    mt(new Language_ISO_639_1("Maltese","mt")),
    mi(new Language_ISO_639_1("Maori","mi")),
    mr(new Language_ISO_639_1("Marathi","mr")),
    mh(new Language_ISO_639_1("Marshallese","mh")),
    mo(new Language_ISO_639_1("Moldavian","mo")),
    mn(new Language_ISO_639_1("Mongolian","mn")),
    na(new Language_ISO_639_1("Nauru","na")),
    nv(new Language_ISO_639_1("Navajo","nv")),
    ng(new Language_ISO_639_1("Ndonga","ng")),
    nd(new Language_ISO_639_1("Northern Ndebele","nd")),
    ne(new Language_ISO_639_1("Nepali","ne")),
    no(new Language_ISO_639_1("Norwegian","no")),
    nb(new Language_ISO_639_1("Norwegian bokmål","nb")),
    nn(new Language_ISO_639_1("Norwegian nynorsk","nn")),
    oc(new Language_ISO_639_1("Occitan","oc")),
    oj(new Language_ISO_639_1("Ojibwe","oj")),
    cu(new Language_ISO_639_1("Church Slavonic, Old Slavonic, Old Church Slavonic","cu")),
//    cu(new Language_ISO_639_1("Old Bulgarian","cu")),
    or(new Language_ISO_639_1("Oriya","or")),
    om(new Language_ISO_639_1("Oromo (Afaan Oromo)","om")),
    os(new Language_ISO_639_1("Ossetian","os")),
    pi(new Language_ISO_639_1("Pāli","pi")),
    ps(new Language_ISO_639_1("Pashto, Pushto","ps")),
    fa(new Language_ISO_639_1("Persian (Farsi)","fa")),
    pl(new Language_ISO_639_1("Polish","pl")),
    pt(new Language_ISO_639_1("Portuguese","pt")),
    pa(new Language_ISO_639_1("Punjabi (Eastern)","pa")),
    qu(new Language_ISO_639_1("Quechua","qu")),
    rm(new Language_ISO_639_1("Romansh","rm")),
    ro(new Language_ISO_639_1("Romanian","ro")),
    ru(new Language_ISO_639_1("Russian","ru")),
    se(new Language_ISO_639_1("Sami","se")),
    sm(new Language_ISO_639_1("Samoan","sm")),
    sg(new Language_ISO_639_1("Sango","sg")),
    sa(new Language_ISO_639_1("Sanskrit","sa")),
    sr(new Language_ISO_639_1("Serbian","sr")),
    sh(new Language_ISO_639_1("Serbo-Croatian","sh")),
    st(new Language_ISO_639_1("Sesotho","st")),
    tn(new Language_ISO_639_1("Setswana","tn")),
    sn(new Language_ISO_639_1("Shona","sn")),
    ii(new Language_ISO_639_1("Sichuan Yi, Nuosu","ii")),
    sd(new Language_ISO_639_1("Sindhi","sd")),
    si(new Language_ISO_639_1("Sinhalese","si")),
    ss(new Language_ISO_639_1("Swati","ss")),
    sk(new Language_ISO_639_1("Slovak","sk")),
    sl(new Language_ISO_639_1("Slovenian","sl")),
    so(new Language_ISO_639_1("Somali","so")),
    nr(new Language_ISO_639_1("Southern Ndebele","nr")),
    es(new Language_ISO_639_1("Spanish","es")),
    su(new Language_ISO_639_1("Sundanese","su")),
    sw(new Language_ISO_639_1("Swahili (Kiswahili)","sw")),
    sv(new Language_ISO_639_1("Swedish","sv")),
    tl(new Language_ISO_639_1("Tagalog","tl")),
    ty(new Language_ISO_639_1("Tahitian","ty")),
    tg(new Language_ISO_639_1("Tajik","tg")),
    ta(new Language_ISO_639_1("Tamil","ta")),
    tt(new Language_ISO_639_1("Tatar","tt")),
    te(new Language_ISO_639_1("Telugu","te")),
    th(new Language_ISO_639_1("Thai","th")),
    bo(new Language_ISO_639_1("Tibetan","bo")),
    ti(new Language_ISO_639_1("Tigrinya","ti")),
    to(new Language_ISO_639_1("Tonga","to")),
    ts(new Language_ISO_639_1("Tsonga","ts")),
    tr(new Language_ISO_639_1("Turkish","tr")),
    tk(new Language_ISO_639_1("Turkmen","tk")),
    tw(new Language_ISO_639_1("Twi","tw")),
    ug(new Language_ISO_639_1("Uyghur","ug")),
    uk(new Language_ISO_639_1("Ukrainian","uk")),
    ur(new Language_ISO_639_1("Urdu","ur")),
    uz(new Language_ISO_639_1("Uzbek","uz")),
    ve(new Language_ISO_639_1("Venda","ve")),
    vi(new Language_ISO_639_1("Vietnamese","vi")),
    vo(new Language_ISO_639_1("Volapük","vo")),
    wa(new Language_ISO_639_1("Wallon","wa")),
    cy(new Language_ISO_639_1("Welsh","cy")),
    wo(new Language_ISO_639_1("Wolof","wo")),
    fy(new Language_ISO_639_1("Western Frisian","fy")),
    xh(new Language_ISO_639_1("Xhosa","xh")),
    yi(new Language_ISO_639_1("Yiddish","yi","ji","yi")),
    yo(new Language_ISO_639_1("Yoruba","yo")),
    za(new Language_ISO_639_1("Zhuang, Chuang","za")),
    zu(new Language_ISO_639_1("Zulu","zu")),
    ;
    
    private Language_ISO_639_1 value;

    LanguageCode(Language_ISO_639_1 value)
    {
        this.value = value;
    }

    public Language_ISO_639_1 getValue()
    {
        return this.value;
    }
    
    static class CodeMap extends HashMap<String,LanguageCode>
    {
        private static final long serialVersionUID = 1L;

        public CodeMap()
        {
            for (LanguageCode languageCode : LanguageCode.values())
            {
                put(languageCode.getValue().code,languageCode);
            }
        }
    }
    static private CodeMap CODE_MAP=new CodeMap();
    public static LanguageCode fromCodeISO_639_1(String value)
    {
        return CODE_MAP.get(value);
    }

    static class NameMap extends HashMap<String,LanguageCode>
    {
        public NameMap()
        {
            for (LanguageCode languageCode : LanguageCode.values())
            {
                put(languageCode.name(),languageCode);
            }
        }
        private static final long serialVersionUID = 1L;
    }
    static private NameMap NAME_MAP=new NameMap();
    
    public static LanguageCode fromName(String value)
    {
        return NAME_MAP.get(value);
    }
    
    
}
